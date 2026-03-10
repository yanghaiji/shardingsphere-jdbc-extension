package com.shardingsphere.jdbc.kingbase8.parser.sql;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.apache.shardingsphere.sql.parser.api.ASTNode;
import org.apache.shardingsphere.sql.parser.api.parser.SQLParser;
import org.apache.shardingsphere.sql.parser.autogen.PostgreSQLStatementParser;
import org.apache.shardingsphere.sql.parser.core.ParseASTNode;

/**
 * Kingbase8 SQL 解析器 继承 PostgreSQLStatementParser
 * <p>
 *
 * @author haiji
 *
 */
public class KingBase8SQLParser extends PostgreSQLStatementParser implements SQLParser {

    public KingBase8SQLParser(TokenStream input) {
        super(input);
    }

    /**
     * @return
     */
    @Override
    public ASTNode parse() {
        return new ParseASTNode(this.execute(), (CommonTokenStream) this.getTokenStream());
    }
}
