package org.jetbrains.squash.dialects.postgres

import org.jetbrains.squash.connection.*
import org.jetbrains.squash.drivers.*
import org.jetbrains.squash.schema.*
import java.sql.*

class PgTransaction(connection: DatabaseConnection, connector: () -> Connection) : JDBCTransaction(connection, connector) {
    override fun databaseSchema(): DatabaseSchema = PgDatabaseSchema(connection.dialect, jdbcConnection)
}