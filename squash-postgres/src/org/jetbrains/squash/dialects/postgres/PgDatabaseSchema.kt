package org.jetbrains.squash.dialects.postgres

import org.jetbrains.squash.*
import org.jetbrains.squash.drivers.*
import java.sql.*

class PgConnection(connector: () -> Connection) : JDBCConnection(PostgresDialect, connector) {
    override fun createTransaction(): Transaction = PgTransaction(this, connector)

    companion object {
        fun create(url: String, driver: String, user: String = "", password: String = ""): DatabaseConnection {
            Class.forName(driver).newInstance()
            return PgConnection { DriverManager.getConnection(url, user, password) }
        }
    }

}

class PgTransaction(connection: DatabaseConnection, connector: () -> Connection) : JDBCTransaction(connection, connector) {
    override fun querySchema(): DatabaseSchema = PgDatabaseSchema(jdbcConnection)
}

class PgDatabaseSchema(connection: Connection) : JDBCDatabaseSchema(connection) {
    override fun tables(): Sequence<DatabaseSchema.Table> {
        val schema = currentSchema()
        val statement = connection.prepareStatement("SELECT c.relname AS TABLE_NAME FROM pg_catalog.pg_namespace n, pg_catalog.pg_class c WHERE c.relnamespace = n.oid AND c.relkind = 'r' AND n.nspname = '$schema'")
        val resultSet = statement.executeQuery()
        return JDBCResponse(resultSet).rows.map {
            val name = it.get<String>("TABLE_NAME").toUpperCase()
            Table(name, this)
        }
    }
}