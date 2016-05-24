package org.jetbrains.squash.dialects.h2

import org.jetbrains.squash.drivers.*
import org.jetbrains.squash.schema.*

class H2Transaction(connection: JDBCConnection) : JDBCTransaction(connection) {
    override fun databaseSchema(): DatabaseSchema = H2DatabaseSchema(this)
}