package org.jetbrains.squash.dialects.h2

import org.h2.jdbcx.*
import org.jetbrains.squash.*
import org.jetbrains.squash.dialect.*
import org.jetbrains.squash.drivers.*
import org.jetbrains.squash.schema.*
import java.sql.*

class H2Transaction(connection: DatabaseConnection, connector: () -> Connection) : JDBCTransaction(connection, connector) {
    override fun databaseSchema(): DatabaseSchema = H2DatabaseSchema(connection.dialect, jdbcConnection)
}

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

class H2Connection(connector: () -> Connection) : JDBCConnection(H2Dialect, connector) {
    override fun createTransaction(): Transaction = H2Transaction(this, connector)

    companion object {
        fun createMemoryConnection(catalogue: String = "", user: String = "", password: String = ""): DatabaseConnection {
            val pool = JdbcConnectionPool.create("jdbc:h2:mem:$catalogue", user, password);
            return H2Connection { pool.connection }
        }
    }
}
