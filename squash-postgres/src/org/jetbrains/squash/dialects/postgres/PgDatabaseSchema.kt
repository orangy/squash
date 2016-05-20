package org.jetbrains.squash.dialects.postgres

import org.jetbrains.squash.*
import org.jetbrains.squash.dialect.*
import org.jetbrains.squash.drivers.*
import org.jetbrains.squash.schema.*
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
    override fun databaseSchema(): DatabaseSchema = PgDatabaseSchema(connection.dialect, jdbcConnection)
}

class PgDatabaseSchema(dialect: SQLDialect, jdbcConnection: Connection) : JDBCDatabaseSchema(dialect, jdbcConnection) {
    override fun tables(): Sequence<DatabaseSchema.SchemaTable> {
        val schema = currentSchema()
        val tableTypes = if (schema.startsWith("pg_temp_")) arrayOf("TEMPORARY TABLE") else arrayOf("TABLE")
        val resultSet = metadata.getTables(catalogue, currentSchema(), null, tableTypes)
        return JDBCResponse(resultSet).rows.map { SchemaTable(it.get<String>("TABLE_NAME"), this) }
    }
}