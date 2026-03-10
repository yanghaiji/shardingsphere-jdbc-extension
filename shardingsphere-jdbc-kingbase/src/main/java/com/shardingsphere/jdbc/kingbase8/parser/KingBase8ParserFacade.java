package com.shardingsphere.jdbc.kingbase8.parser;

import com.shardingsphere.jdbc.kingbase8.enums.DbType;
import com.shardingsphere.jdbc.kingbase8.parser.sql.KingBase8SQLLexer;
import com.shardingsphere.jdbc.kingbase8.parser.sql.KingBase8SQLParser;
import org.apache.shardingsphere.sql.parser.api.parser.SQLLexer;
import org.apache.shardingsphere.sql.parser.api.parser.SQLParser;
import org.apache.shardingsphere.sql.parser.spi.DialectSQLParserFacade;

/**
 * Kingbase8 SQL 解析器工厂
 * <p>
 *
 * @author haiji
 */
public final class KingBase8ParserFacade implements DialectSQLParserFacade {
    /**
     * 获取 SQL 词法解析器类
     *
     * @return
     */
    public Class<? extends SQLLexer> getLexerClass() {
        return KingBase8SQLLexer.class;
    }

    /**
     * 获取 SQL 解析器类
     *
     * @return
     */
    public Class<? extends SQLParser> getParserClass() {
        return KingBase8SQLParser.class;
    }

    /**
     * 获取数据库类型
     *
     * @return
     */
    public String getDatabaseType() {
        return DbType.KING_BASE8.name();
    }
}
