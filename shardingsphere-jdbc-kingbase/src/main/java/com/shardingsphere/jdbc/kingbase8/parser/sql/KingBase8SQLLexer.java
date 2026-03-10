package com.shardingsphere.jdbc.kingbase8.parser.sql;

import org.antlr.v4.runtime.CharStream;
import org.apache.shardingsphere.sql.parser.api.parser.SQLLexer;
import org.apache.shardingsphere.sql.parser.autogen.PostgreSQLStatementLexer;


/**
 * Kingbase8 SQL lexer.
 * <p>
 *
 * @author haiji
 */
public class KingBase8SQLLexer extends PostgreSQLStatementLexer implements SQLLexer {

    public KingBase8SQLLexer(CharStream input) {
        super(input);
    }

}
