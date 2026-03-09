package com.shardingsphere.jdbc.nacos.config.driver;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.shardingsphere.jdbc.nacos.config.NacosShardingSphereURL;
import com.shardingsphere.jdbc.nacos.config.NacosShardingSphereURLLoadEngine;
import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Driver data source cache.
 * <p>
 * 参考{@link org.apache.shardingsphere.driver.jdbc.core.driver.DriverDataSourceCache}进行适配自定义
 *
 * @author haiji
 */
public final class NacosDriverDataSourceCache {

    private final Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<>();
    // 记录已注册监听器的 URL，避免重复注册
    private final Set<String> registeredListeners = ConcurrentHashMap.newKeySet();
    private static final Logger log = LoggerFactory.getLogger(NacosDriverDataSourceCache.class);

    /**
     * 获取数据源，使用完整 URL 作为缓存键。
     *
     * @param fullUrl   完整 JDBC URL（包含前缀）
     * @param urlPrefix URL 前缀，用于解析实际配置路径
     * @return 数据源
     */
    public DataSource get(final String fullUrl, final String urlPrefix) {
        return dataSourceMap.computeIfAbsent(fullUrl, url -> {
            // 解析 URL（去掉前缀部分）
            NacosShardingSphereURL parsedUrl = NacosShardingSphereURL.parse(url.substring(urlPrefix.length()));
            return createDataSource(fullUrl, parsedUrl);
        });
    }

    /**
     * 创建数据源实例。
     *
     * @param fullUrl   完整 JDBC URL（包含前缀）
     * @param url       解析后的 NacosShardingSphereURL
     * @return 数据源实例
     */
    private DataSource createDataSource(final String fullUrl, final NacosShardingSphereURL url) {
        try {
            String configurationSubject = url.getConfigurationSubject();
            DataSource dataSource;

            // 处理多配置合并（逗号分隔）
            if (configurationSubject.contains(",")) {
                ByteArrayOutputStream baos = getOutputStream(url, configurationSubject);
                dataSource = YamlShardingSphereDataSourceFactory.createDataSource(baos.toByteArray());
            } else {
                NacosShardingSphereURLLoadEngine urlLoadEngine = new NacosShardingSphereURLLoadEngine(url);
                dataSource = YamlShardingSphereDataSourceFactory.createDataSource(urlLoadEngine.loadContent());
            }

            // 注册 Nacos 监听器，实现动态刷新
            registerListener(fullUrl, url);

            return dataSource;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load configuration from Nacos", ex);
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to create ShardingSphere DataSource", ex);
        }
    }

    /**
     * 获取 ByteArrayOutputStream 对象，用于合并多个配置内容
     */
    private static ByteArrayOutputStream getOutputStream(NacosShardingSphereURL url, String configurationSubject) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String[] subjects = configurationSubject.split(",");
        String sourceType = url.getSourceType();
        Properties queryProps = url.getQueryProps();

        for (String subject : subjects) {
            NacosShardingSphereURL urlEach = new NacosShardingSphereURL(sourceType, subject, queryProps);
            NacosShardingSphereURLLoadEngine urlLoadEngine = new NacosShardingSphereURLLoadEngine(urlEach);
            byte[] bytes = urlLoadEngine.loadContent();
            baos.write(bytes);
        }
        return baos;
    }

    /**
     * 为当前 URL 注册 Nacos 配置监听器
     */
    private void registerListener(String fullUrl, NacosShardingSphereURL url) {
        if (!registeredListeners.add(fullUrl)) {
            return; // 已经注册过，直接返回
        }

        String configurationSubject = url.getConfigurationSubject();
        Properties queryProps = url.getQueryProps();
        String group = queryProps.getProperty(Constants.GROUP, Constants.DEFAULT_GROUP);

        try {
            ConfigService configService = NacosFactory.createConfigService(queryProps);

            // 根据是否多配置决定注册方式
            if (configurationSubject.contains(",")) {
                String[] subjects = configurationSubject.split(",");
                for (String subject : subjects) {
                    configService.addListener(subject, group, new Listener() {
                        @Override
                        public Executor getExecutor() {
                            // 使用单线程执行器，保证顺序处理
                            return Executors.newSingleThreadExecutor();
                        }

                        @Override
                        public void receiveConfigInfo(String configInfo) {
                            // 任一子配置变化，触发整个数据源刷新
                            refreshDataSource(fullUrl, url);
                        }
                    });
                }
            } else {
                configService.addListener(configurationSubject, group, new Listener() {
                    @Override
                    public Executor getExecutor() {
                        return Executors.newSingleThreadExecutor();
                    }

                    @Override
                    public void receiveConfigInfo(String configInfo) {
                        refreshDataSource(fullUrl, url);
                    }
                });
            }
            log.info("Nacos listener registered for URL: {}", fullUrl);
        } catch (NacosException e) {
            // 注册失败时移除标记，允许后续重试
            registeredListeners.remove(fullUrl);
            log.error("Failed to register Nacos listener for URL: {}", fullUrl, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 刷新数据源：重新加载配置并原子替换缓存中的 DataSource
     */
    private void refreshDataSource(String fullUrl, NacosShardingSphereURL url) {
        try {
            DataSource newDataSource = createDataSource(fullUrl, url); // 重新创建
            dataSourceMap.compute(fullUrl, (key, oldValue) -> {
                if (oldValue != null) {
                    // 可在此处优雅关闭旧数据源（需根据连接池实现）
                    // closeQuietly(oldValue);
                }
                return newDataSource;
            });
            log.info("DataSource refreshed for URL: {}", fullUrl);
        } catch (Exception e) {
            log.error("Failed to refresh DataSource for URL: {}", fullUrl, e);
        }
    }

    public Map<String, DataSource> getDataSourceMap() {
        return dataSourceMap;
    }
}