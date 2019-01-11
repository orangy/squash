package org.jetbrains.squash.drivers.postgres.jdbc.tests

import org.jetbrains.squash.tests.*

class PgGraphTests : GraphTests(), DatabaseTests by PgJdbcDatabaseTests()