package org.jetbrains.squash.drivers.postgres.jdbc.tests

import org.jetbrains.squash.tests.*

class PgDefinitionTests : DefinitionTests(), DatabaseTests by PgJdbcDatabaseTests()