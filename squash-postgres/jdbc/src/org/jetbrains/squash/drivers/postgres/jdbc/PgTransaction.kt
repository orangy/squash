package org.jetbrains.squash.drivers.postgres.jdbc

import org.jetbrains.squash.drivers.jdbc.*
import org.jetbrains.squash.schema.*

class PgTransaction(connection: JDBCConnection) : JDBCTransaction(connection) {
    override suspend fun databaseSchema(): DatabaseSchema = PgDatabaseSchema(this)
}