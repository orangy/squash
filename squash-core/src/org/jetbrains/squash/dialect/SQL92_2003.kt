package org.jetbrains.squash.dialect

object SQL92_2003 {
    val keywords = setOf(
            "ABSOLUTE", "ACTION", "ADD", "AFTER", "ALL", "ALLOCATE", "ALTER", "AND",
            "ANY", "ARE", "ARRAY", "AS", "ASC", "ASENSITIVE", "ASSERTION", "ASYMMETRIC",
            "AT", "ATOMIC", "AUTHORIZATION", "AVG", "BEFORE", "BEGIN", "BETWEEN", "BIGINT",
            "BINARY", "BIT", "BIT_LENGTH", "BLOB", "BOOLEAN", "BOTH", "BREADTH", "BY",
            "CALL", "CALLED", "CASCADE", "CASCADED", "CASE", "CAST", "CATALOG", "CHAR",
            "CHARACTER", "CHARACTER_LENGTH", "CHAR_LENGTH", "CHECK", "CLOB", "CLOSE", "COALESCE", "COLLATE",
            "COLLATION", "COLUMN", "COMMIT", "CONDITION", "CONNECT", "CONNECTION", "CONSTRAINT", "CONSTRAINTS",
            "CONSTRUCTOR", "CONTAINS", "CONTINUE", "CONVERT", "CORRESPONDING", "COUNT", "CREATE", "CROSS",
            "CUBE", "CURRENT", "CURRENT_DATE", "CURRENT_DEFAULT_TRANSFORM_GROUP", "CURRENT_PATH", "CURRENT_ROLE", "CURRENT_TIME", "CURRENT_TIMESTAMP",
            "CURRENT_TRANSFORM_GROUP_FOR_TYPE", "CURRENT_USER", "CURSOR", "CYCLE", "DATA", "DATE", "DAY", "DEALLOCATE",
            "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DEFERRABLE", "DEFERRED", "DELETE", "DEPTH",
            "DEREF", "DESC", "DESCRIBE", "DESCRIPTOR", "DETERMINISTIC", "DIAGNOSTICS", "DISCONNECT", "DISTINCT",
            "DO", "DOMAIN", "DOUBLE", "DROP", "DYNAMIC", "EACH", "ELEMENT", "ELSE",
            "ELSEIF", "END", "END-EXEC", "EQUALS", "ESCAPE", "EXCEPT", "EXCEPTION", "EXEC",
            "EXECUTE", "EXISTS", "EXIT", "EXTERNAL", "EXTRACT", "FALSE", "FETCH", "FILTER",
            "FIRST", "FLOAT", "FOR", "FOREIGN", "FORTRAN", "FOUND", "FREE", "FROM",
            "FULL", "FUNCTION", "GENERAL", "GET", "GLOBAL", "GO", "GOTO", "GRANT",
            "GROUP", "GROUPING", "HANDLER", "HAVING", "HOLD", "HOUR", "IDENTITY", "IF",
            "IMMEDIATE", "IN", "INCLUDE", "INDEX", "INDICATOR", "INITIALLY", "INNER", "INOUT",
            "INPUT", "INSENSITIVE", "INSERT", "INT", "INTEGER", "INTERSECT", "INTERVAL", "INTO",
            "IS", "ISOLATION", "ITERATE", "JOIN", "KEY", "LANGUAGE", "LARGE", "LAST",
            "LATERAL", "LEADING", "LEAVE", "LEFT", "LEVEL", "LIKE", "LOCAL", "LOCALTIME",
            "LOCALTIMESTAMP", "LOCATOR", "LOOP", "LOWER", "MAP", "MATCH", "MAX", "MEMBER",
            "MERGE", "METHOD", "MIN", "MINUTE", "MODIFIES", "MODULE", "MONTH", "MULTISET",
            "NAMES", "NATIONAL", "NATURAL", "NCHAR", "NCLOB", "NEW", "NEXT", "NO",
            "NONE", "NOT", "NULL", "NULLIF", "NUMERIC", "OBJECT", "OCTET_LENGTH", "OF",
            "OLD", "ON", "ONLY", "OPEN", "OPTION", "OR", "ORDER", "ORDINALITY",
            "OUT", "OUTER", "OUTPUT", "OVER", "OVERLAPS", "PAD", "PARAMETER", "PARTIAL",
            "PARTITION", "PASCAL", "PATH", "POSITION", "PRECISION", "PREPARE", "PRESERVE", "PRIMARY",
            "PRIOR", "PRIVILEGES", "PROCEDURE", "PUBLIC", "RANGE", "READ", "READS", "REAL",
            "RECURSIVE", "REF", "REFERENCES", "REFERENCING", "RELATIVE", "RELEASE", "REPEAT", "RESIGNAL",
            "RESTRICT", "RESULT", "RETURN", "RETURNS", "REVOKE", "RIGHT", "ROLE", "ROLLBACK",
            "ROLLUP", "ROUTINE", "ROW", "ROWS", "SAVEPOINT", "SCHEMA", "SCOPE", "SCROLL",
            "SEARCH", "SECOND", "SECTION", "SELECT", "SENSITIVE", "SESSION", "SESSION_USER", "SET",
            "SETS", "SIGNAL", "SIMILAR", "SIZE", "SMALLINT", "SOME", "SPACE", "SPECIFIC",
            "SPECIFICTYPE", "SQL", "SQLCA", "SQLCODE", "SQLERROR", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING",
            "START", "STATE", "STATIC", "SUBMULTISET", "SUBSTRING", "SUM", "SYMMETRIC", "SYSTEM",
            "SYSTEM_USER", "TABLE", "TABLESAMPLE", "TEMPORARY", "THEN", "TIME", "TIMESTAMP", "TIMEZONE_HOUR",
            "TIMEZONE_MINUTE", "TO", "TRAILING", "TRANSACTION", "TRANSLATE", "TRANSLATION", "TREAT", "TRIGGER",
            "TRIM", "TRUE", "UNDER", "UNDO", "UNION", "UNIQUE", "UNKNOWN", "UNNEST",
            "UNTIL", "UPDATE", "UPPER", "USAGE", "USER", "USING", "VALUE", "VALUES",
            "VARCHAR", "VARYING", "VIEW", "WHEN", "WHENEVER", "WHERE", "WHILE", "WINDOW",
            "WITH", "WITHIN", "WITHOUT", "WORK", "WRITE", "YEAR", "ZONE"
    )

    init {
/*
        val items = sql92Keywords.distinct().sorted()
        items.forEachIndexed { index, item ->
            print("\"$item\", ")
            if (index % 8 == 7)
                println()
        }
        println()
*/
    }
}


