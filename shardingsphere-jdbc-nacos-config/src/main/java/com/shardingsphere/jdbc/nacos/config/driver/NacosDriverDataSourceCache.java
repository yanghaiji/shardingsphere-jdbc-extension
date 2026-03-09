package com.shardingsphere.jdbc.nacos.config.driver;

import com.shardingsphere.jdbc.nacos.config.NacosShardingSphereURL;
import com.shardingsphere.jdbc.nacos.config.NacosShardingSphereURLLoadEngine;
import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Driver data source cache.
 * <p>
 * 参考{@link org.apache.shardingsphere.driver.jdbc.core.driver.DriverDataSourceCache}进行适配自定义
 *
 * @author haiji
 */
public final class NacosDriverDataSourceCache {

    private final Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<>();

    /**
     * 获取数据源，使用 URL 作为缓存键。
     *
     * @param url       完整 URL
     * @param urlPrefix URL 前缀，用于解析实际配置路径
     * @return 数据源
     */
    public DataSource get(final String url, final String urlPrefix) {
        return dataSourceMap.computeIfAbsent(url, driverUrl ->
        {
            try {
                return createDataSource(NacosShardingSphereURL.parse(driverUrl.substring(urlPrefix.length())));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 创建数据源。
     *
     * @param url 完整 URL
     * @return 数据源
     * @throws Exception 创建数据源异常
     */
    private DataSource createDataSource(final NacosShardingSphereURL url) throws Exception {
        try {
            String configurationSubject = url.getConfigurationSubject();

            // 当配置主题包含逗号时，表示多个配置文件需要合并
            // 格式示例: jdbc:shardingsphere:nacos:sharding.yaml,sharding2.yaml?serverAddr=${spring.cloud.nacos.config.server-addr}&namespace=${spring.cloud.nacos.config.namespace}
            if (configurationSubject.contains(",")) {
                ByteArrayOutputStream baos = getBaos(url, configurationSubject);
                return YamlShardingSphereDataSourceFactory.createDataSource(baos.toByteArray());
            } else {
                // 单个配置文件
                NacosShardingSphereURLLoadEngine urlLoadEngine = new NacosShardingSphereURLLoadEngine(url);
                return YamlShardingSphereDataSourceFactory.createDataSource(urlLoadEngine.loadContent());
            }
        } catch (final IOException ex) {
            // 将 IO 异常包装为 SQL 异常
            throw new SQLException(ex);
        }
    }

    /**
     * 获取 ByteArrayOutputStream。
     *
     * @param url                   完整 URL
     * @param configurationSubject 配置主题
     * @return ByteArrayOutputStream
     * @throws IOException IO 异常
     */
    private static ByteArrayOutputStream getBaos(NacosShardingSphereURL url, String configurationSubject) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String[] split = configurationSubject.split(",");
        String sourceType = url.getSourceType();
        Properties queryProps = url.getQueryProps();

        for (String each : split) {
            NacosShardingSphereURL urlEach = new NacosShardingSphereURL(sourceType, each, queryProps);
            NacosShardingSphereURLLoadEngine urlLoadEngine = new NacosShardingSphereURLLoadEngine(urlEach);
            byte[] bytes = urlLoadEngine.loadContent();
            baos.write(bytes);  // 合并内容
        }
        return baos;
    }

    /**
     * 获取数据源缓存。
     *
     * @return 数据源缓存
     */
    public Map<String, DataSource> getDataSourceMap() {
        return dataSourceMap;
    }
}