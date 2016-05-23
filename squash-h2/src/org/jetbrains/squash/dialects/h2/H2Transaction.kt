package org.jetbrains.squash.dialects.h2

import org.jetbrains.squash.connection.*
import org.jetbrains.squash.drivers.*
import org.jetbrains.squash.schema.*
import java.sql.*

class H2Transaction(connection: JDBCConnection) : JDBCTransaction(connection) {
    override fun databaseSchema(): DatabaseSchema = H2DatabaseSchema(connection.dialect, this)
}