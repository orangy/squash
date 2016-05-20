package org.jetbrains.squash.dialects.h2

import org.jetbrains.squash.*
import org.jetbrains.squash.drivers.*
import org.jetbrains.squash.schema.*
import java.sql.*

class H2Transaction(connection: DatabaseConnection, connector: () -> Connection) : JDBCTransaction(connection, connector) {
    override fun databaseSchema(): DatabaseSchema = H2DatabaseSchema(connection.dialect, jdbcConnection)
}