package org.jetbrains.squash.dialects.postgres.tests

import org.jetbrains.squash.tests.*

class PgGraphTests : GraphTests(), DatabaseTests by PgDatabaseTests()