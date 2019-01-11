package org.jetbrains.squash.drivers.postgres.jdbc.tests

import org.jetbrains.squash.tests.*

class PgModificationTests : ModificationTests(), DatabaseTests by PgJdbcDatabaseTests()