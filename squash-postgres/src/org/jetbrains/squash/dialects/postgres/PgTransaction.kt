package org.jetbrains.squash.dialects.postgres

import org.jetbrains.squash.drivers.*
import org.jetbrains.squash.schema.*

class PgTransaction(connection: JDBCConnection) : JDBCTransaction(connection) {
    override suspend fun databaseSchema(): DatabaseSchema = PgDatabaseSchema(this)
}