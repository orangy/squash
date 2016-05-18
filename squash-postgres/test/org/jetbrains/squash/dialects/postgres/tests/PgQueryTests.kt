package org.jetbrains.squash.dialects.postgres.tests

import org.jetbrains.squash.tests.*

class PgQueryTests : QueryTests(), DatabaseTests by PgDatabaseTests()