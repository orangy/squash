package org.jetbrains.squash.dialects.h2

import org.jetbrains.squash.dialect.*
import org.jetbrains.squash.drivers.*
import java.sql.*

class H2DatabaseSchema(dialect: SQLDialect, jdbcConnection: Connection) : JDBCDatabaseSchema(dialect, jdbcConnection) {
    override fun currentSchema(): String {
        val preparedStatement = jdbcConnection.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.SESSION_STATE WHERE KEY='SCHEMA_SEARCH_PATH'")
        val resultSet = preparedStatement.executeQuery()
        if (resultSet.next()) {
            return resultSet.getString(1)
        }
        return ""
    }
}