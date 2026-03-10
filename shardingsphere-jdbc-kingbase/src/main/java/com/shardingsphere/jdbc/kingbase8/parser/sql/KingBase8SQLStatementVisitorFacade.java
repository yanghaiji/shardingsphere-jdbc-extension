package com.shardingsphere.jdbc.kingbase8.parser.sql;

import com.shardingsphere.jdbc.kingbase8.enums.DbType;
import org.apache.shardingsphere.sql.parser.api.visitor.statement.type.*;
import org.apache.shardingsphere.sql.parser.postgresql.visitor.statement.type.*;
import org.apache.shardingsphere.sql.parser.spi.SQLStatementVisitorFacade;

/**
 * Kingbase8 SQL statement visitor facade.
 * <p>
 *
 * @author haiji
 */
public class KingBase8SQLStatementVisitorFacade implements SQLStatementVisitorFacade {
    public Class<? extends DMLStatementVisitor> getDMLVisitorClass() {
        return PostgreSQLDMLStatementVisitor.class;
    }

    public Class<? extends DDLStatementVisitor> getDDLVisitorClass() {
        return PostgreSQLDDLStatementVisitor.class;
    }

    public Class<? extends TCLStatementVisitor> getTCLVisitorClass() {
        return PostgreSQLTCLStatementVisitor.class;
    }

    public Class<? extends DCLStatementVisitor> getDCLVisitorClass() {
        return PostgreSQLDCLStatementVisitor.class;
    }

    public Class<? extends DALStatementVisitor> getDALVisitorClass() {
        return PostgreSQLDALStatementVisitor.class;
    }

    public Class<? extends RLStatementVisitor> getRLVisitorClass() {
        return null;
    }

    public String getDatabaseType() {
        return DbType.KING_BASE8.name();
    }
}
