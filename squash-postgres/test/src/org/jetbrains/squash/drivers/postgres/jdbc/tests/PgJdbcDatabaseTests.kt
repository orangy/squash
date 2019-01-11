package org.jetbrains.squash.drivers.postgres.jdbc.tests

import org.jetbrains.squash.drivers.postgres.jdbc.*
import org.jetbrains.squash.drivers.postgres.tests.*

class PgJdbcDatabaseTests : PgDatabaseTests() {
    override fun createConnection() = PgJdbcConnection.create(embeddedPostgresConfig.connectionString)
}