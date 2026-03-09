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
     * Get data source.
     *
     * @param url       URL
     * @param urlPrefix URL prefix
     * @return got data source
     */
    public DataSource get(final String url, final String urlPrefix) {
        if (dataSourceMap.containsKey(url)) {
            return dataSourceMap.get(url);
        }
        return dataSourceMap.computeIfAbsent(url, driverUrl -> createDataSource(NacosShardingSphereURL.parse(driverUrl.substring(urlPrefix.length()))));
    }

    @SuppressWarnings("unchecked")
    private <T extends Throwable> DataSource createDataSource(final NacosShardingSphereURL url) throws T {
        try {
            String configurationSubject = url.getConfigurationSubject();

            /*
             * 当存在多个配置时, 则进行合并
             *
             * 格式为 jdbc:shardingsphere:nacos:sharding.yaml,s# ShardingSphere JDBC Extension

ShardingSphere JDBC 扩展项目，提供对国产数据库（人大金仓）的支持以及 Nacos 配置中心的集成。

## 📦 项目结构

harding2.yaml?serverAddr=${spring.cloud.nacos.config.server-addr}&namespace=${spring.cloud.nacos.config.namespace}
             */
            if (configurationSubject.contains(",")) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                String[] split = configurationSubject.split(",");
                String sourceType = url.getSourceType();
                Properties queryProps = url.getQueryProps();

                for (String each : split) {
                    NacosShardingSphereURL urlEach = new NacosShardingSphereURL(sourceType, each, queryProps);
                    NacosShardingSphereURLLoadEngine urlLoadEngine = new NacosShardingSphereURLLoadEngine(urlEach);
                    byte[] bytes = urlLoadEngine.loadContent();
                    merge(baos, bytes);
                }
                return YamlShardingSphereDataSourceFactory.createDataSource(baos.toByteArray());
            } else {
                // 单个配置
                NacosShardingSphereURLLoadEngine urlLoadEngine = new NacosShardingSphereURLLoadEngine(url);
                return YamlShardingSphereDataSourceFactory.createDataSource(urlLoadEngine.loadContent());
            }
        } catch (final IOException ex) {
            throw (T) new SQLException(ex);
        } catch (final SQLException ex) {
            throw (T) ex;
        }
    }

    public Map<String, DataSource> getDataSourceMap() {
        return dataSourceMap;
    }

    public static void merge(ByteArrayOutputStream baos, byte[]... arrays) {
        for (byte[] arr : arrays) {
            baos.writeBytes(arr);
        }
    }
}