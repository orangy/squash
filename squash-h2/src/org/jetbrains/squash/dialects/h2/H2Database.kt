package org.jetbrains.squash.dialects.h2

import org.h2.jdbcx.*
import org.jetbrains.squash.*
import org.jetbrains.squash.drivers.*
import java.sql.*

class H2Transaction(connection: DatabaseConnection, connector: () -> Connection) : JDBCTransaction(connection, connector) {
    override fun querySchema(): DatabaseSchema = H2DatabaseSchema(jdbcConnection)
}

class H2DatabaseSchema(connection: Connection) : JDBCDatabaseSchema(connection) {
    override fun currentSchema(): String {
        val preparedStatement = connection.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.SESSION_STATE WHERE KEY='SCHEMA_SEARCH_PATH'")
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
