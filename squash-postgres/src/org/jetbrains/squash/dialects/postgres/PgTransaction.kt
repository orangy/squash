package org.jetbrains.squash.dialects.postgres

import org.jetbrains.squash.connection.*
import org.jetbrains.squash.drivers.*
import org.jetbrains.squash.schema.*
import java.sql.*

class PgTransaction(connection: JDBCConnection) : JDBCTransaction(connection) {
    override fun databaseSchema(): DatabaseSchema = PgDatabaseSchema(connection.dialect, this)
}