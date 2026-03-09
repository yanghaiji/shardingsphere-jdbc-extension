
package com.shardingsphere.jdbc.nacos.config.driver;

import org.apache.shardingsphere.driver.exception.DriverRegisterException;
import org.apache.shardingsphere.infra.annotation.HighFrequencyInvocation;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * ShardingSphere driver.
 */
@SuppressWarnings("UseOfJDBCDriverClass")
public final class NacosShardingSphereDriver implements Driver {
    
    private static final String DRIVER_URL_PREFIX = "jdbc:shardingsphere:";
    
    private static final int MAJOR_DRIVER_VERSION = 5;
    
    private static final int MINOR_DRIVER_VERSION = 5;
    
    private final NacosDriverDataSourceCache dataSourceCache = new NacosDriverDataSourceCache();
    
    static {
        try {
            DriverManager.registerDriver(new NacosShardingSphereDriver());
        } catch (final SQLException ex) {
            throw new DriverRegisterException(ex);
        }
    }
    
    @HighFrequencyInvocation(canBeCached = true)
    @Override
    public Connection connect(final String url, final Properties info) throws SQLException {
        return acceptsURL(url) ? dataSourceCache.get(url, DRIVER_URL_PREFIX).getConnection() : null;
    }
    
    @Override
    public boolean acceptsURL(final String url) {
        return null != url && url.startsWith(DRIVER_URL_PREFIX);
    }
    
    @Override
    public DriverPropertyInfo[] getPropertyInfo(final String url, final Properties info) {
        return new DriverPropertyInfo[0];
    }
    
    @Override
    public int getMajorVersion() {
        return MAJOR_DRIVER_VERSION;
    }
    
    @Override
    public int getMinorVersion() {
        return MINOR_DRIVER_VERSION;
    }
    
    @Override
    public boolean jdbcCompliant() {
        return false;
    }
    
    @Override
    public Logger getParentLogger() {
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }
}
