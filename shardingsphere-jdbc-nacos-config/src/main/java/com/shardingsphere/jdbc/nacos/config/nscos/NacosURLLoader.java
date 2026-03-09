package com.shardingsphere.jdbc.nacos.config.nscos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.config.ConfigService;
import org.apache.shardingsphere.infra.url.spi.ShardingSphereURLLoader;

import java.util.Properties;

public class NacosURLLoader implements ShardingSphereURLLoader {

    @Override
    public String load(final String configurationSubject, final Properties queryProps) {

        try {
            ConfigService configService = NacosFactory.createConfigService(queryProps);

            return configService.getConfig(
                    configurationSubject,
                    queryProps.getProperty(Constants.GROUP, Constants.DEFAULT_GROUP),
                    500
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to load Nacos configuration: " + e.getMessage(), e);
        }
    }

    @Override
    public String getType() {
        return "nacos:";
    }
}
