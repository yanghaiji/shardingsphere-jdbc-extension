

package com.shardingsphere.jdbc.nacos.config;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import java.util.Properties;

/**
 * Nacos sharding sphere URL.
 */
public final class NacosShardingSphereURL {
    private final String sourceType;
    private final String configurationSubject;
    private final Properties queryProps;

    public static NacosShardingSphereURL parse(String url) {
        String sourceType = parseSourceType(url);
        return new NacosShardingSphereURL(sourceType, parseConfigurationSubject(url.substring(sourceType.length())), parseProperties(url));
    }

    private static String parseSourceType(String url) {
        return url.substring(0, url.indexOf(58) + 1);
    }

    private static String parseConfigurationSubject(String url) {
        String result = url.substring(0, url.contains("?") ? url.indexOf(63) : url.length());
        Preconditions.checkArgument(!result.isEmpty(), "Configuration subject is required in URL.");
        return result;
    }

    private static Properties parseProperties(String url) {
        if (!url.contains("?")) {
            return new Properties();
        } else {
            String queryProps = url.substring(url.indexOf(63) + 1);
            if (Strings.isNullOrEmpty(queryProps)) {
                return new Properties();
            } else {
                String[] pairs = queryProps.split("&");
                Properties result = new Properties();

                for(String each : pairs) {
                    int index = each.indexOf("=");
                    if (index > 0) {
                        result.put(each.substring(0, index), each.substring(index + 1));
                    }
                }

                return result;
            }
        }
    }

    public NacosShardingSphereURL(String sourceType, String configurationSubject, Properties queryProps) {
        this.sourceType = sourceType;
        this.configurationSubject = configurationSubject;
        this.queryProps = queryProps;
    }

    public String getSourceType() {
        return this.sourceType;
    }

    public String getConfigurationSubject() {
        return this.configurationSubject;
    }


    public Properties getQueryProps() {
        return this.queryProps;
    }
}
