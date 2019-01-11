package org.jetbrains.squash.drivers.postgres.jdbc.tests

import org.jetbrains.squash.tests.*

class PgQueryTests : QueryTests(), DatabaseTests by PgDatabaseTests()