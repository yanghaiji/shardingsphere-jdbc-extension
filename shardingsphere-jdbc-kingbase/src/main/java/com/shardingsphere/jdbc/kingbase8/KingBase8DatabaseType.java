package com.shardingsphere.jdbc.kingbase8;

import com.shardingsphere.jdbc.kingbase8.enums.DbType;
import org.apache.shardingsphere.infra.database.core.type.DatabaseType;

import java.util.Collection;
import java.util.Collections;

/**
 * Kingbase8 数据库类型扩展 (ShardingSphere 5.5.2)
 * <p>
 *
 * @author haiji
 */
public final class KingBase8DatabaseType implements DatabaseType {


    /**
     * 获取 JDBC URL 前缀
     */
    @Override
    public Collection<String> getJdbcUrlPrefixes() {
        // 注册 jdbc:kingbase8: 前缀，让 ShardingSphere 能识别
        return Collections.singleton("jdbc:kingbase8:");
    }

    /**
     * 支持的数据库类型
     */
    @Override
    public String getType() {
        return DbType.KING_BASE8.name();
    }

}