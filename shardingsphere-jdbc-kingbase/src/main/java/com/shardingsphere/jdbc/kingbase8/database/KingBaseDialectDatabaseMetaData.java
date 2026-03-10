package com.shardingsphere.jdbc.kingbase8.database;

import com.cedarsoftware.util.CaseInsensitiveMap;
import com.shardingsphere.jdbc.kingbase8.constant.DatabaseMetaData;
import com.shardingsphere.jdbc.kingbase8.enums.DbType;
import org.apache.shardingsphere.infra.database.core.metadata.database.DialectDatabaseMetaData;
import org.apache.shardingsphere.infra.database.core.metadata.database.enums.NullsOrderType;
import org.apache.shardingsphere.infra.database.core.metadata.database.enums.QuoteCharacter;

import java.util.*;

/**
 * Kingbase8 数据库元数据
 *
 * <p>
 *
 * @author haiji
 */
public class KingBaseDialectDatabaseMetaData implements DialectDatabaseMetaData {

    /**
     * 存储过程参数的分隔符
     */
    private static final Set<String> RESERVED_WORDS = new HashSet<>(DatabaseMetaData.DATA_BASE_RESERVED_WORDS);

    public QuoteCharacter getQuoteCharacter() {
        return QuoteCharacter.QUOTE;
    }

    /**
     * 获取额外的数据类型
     * <p>
     * 这里顶的数据类型的顺序，必须与 {@link java.sql.Types}  / {@link java.sql.JDBCType}中保持一致
     *
     * @return 额外的数据类型
     */
    @Override
    public Map<String, Integer> getExtraDataTypes() {
        Map<String, Integer> result = new CaseInsensitiveMap<>();
        result.put("SMALLINT", 5);
        result.put("INT", 4);
        result.put("INTEGER", 4);
        result.put("BIGINT", -5);
        result.put("DECIMAL", 3);
        result.put("NUMERIC", 2);
        result.put("REAL", 7);
        result.put("BOOL", 16);
        result.put("CHARACTER VARYING", 12);
        return result;
    }

    /**
     * 获取默认的 NULL 排序类型
     *
     * @return NULL 排序类型
     */
    @Override
    public NullsOrderType getDefaultNullsOrderType() {
        return NullsOrderType.HIGH;
    }

    /**
     * 判断是否为保留字
     *
     * @param identifier 待判断的标识符
     * @return 是否为保留字
     */
    @Override
    public boolean isReservedWord(String identifier) {
        return RESERVED_WORDS.contains(identifier.toUpperCase());
    }

    /**
     * 是否支持 schema
     *
     * @return 是否支持 schema
     */
    @Override
    public boolean isSchemaAvailable() {
        return true;
    }

    /**
     * 获取默认的 schema
     *
     * @return 默认的 schema
     */
    @Override
    public Optional<String> getDefaultSchema() {
        return Optional.of("public");
    }

    @Override
    public String getDatabaseType() {
        return DbType.KING_BASE8.name();
    }
}