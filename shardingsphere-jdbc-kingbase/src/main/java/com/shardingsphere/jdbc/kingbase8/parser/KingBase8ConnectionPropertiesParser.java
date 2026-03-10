package com.shardingsphere.jdbc.kingbase8.parser;

import com.shardingsphere.jdbc.kingbase8.enums.DbType;
import org.apache.shardingsphere.infra.database.core.connector.ConnectionProperties;
import org.apache.shardingsphere.infra.database.core.connector.ConnectionPropertiesParser;
import org.apache.shardingsphere.infra.database.core.connector.StandardConnectionProperties;
import org.apache.shardingsphere.infra.database.core.connector.url.JdbcUrl;
import org.apache.shardingsphere.infra.database.core.connector.url.StandardJdbcUrlParser;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Kingbase8 连接属性解析器, 用于解析 JDBC URL(用Postgresql的方式解析)
 * <p>
 *
 * @author haiji
 */
public final class KingBase8ConnectionPropertiesParser implements ConnectionPropertiesParser {

    /**
     * 获取当前数据库Schema的正则表达式
     */
    private static final Pattern pattern = Pattern.compile("(?:\\?|&)currentSchema=([^&]*)");


    @Override
    public String getDatabaseType() {
        return DbType.KING_BASE8.name();
    }


    /**
     * Parse URL to connection properties.
     *
     * @param url      URL of data source
     * @param username username of data source
     * @param catalog  catalog of data source
     * @return connection properties
     */
    @Override
    public ConnectionProperties parse(String url, String username, String catalog) {
        JdbcUrl jdbcUrl = (new StandardJdbcUrlParser()).parse(url);

        return new StandardConnectionProperties(
                jdbcUrl.getHostname(),
                jdbcUrl.getPort(5432),
                jdbcUrl.getDatabase(),
                getCurrentSchema(url),
                jdbcUrl.getQueryProperties(), new Properties());
    }

    /**
     * 获取当前数据库Schema
     *
     * @param url
     * @return
     */
    private String getCurrentSchema(String url) {
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}