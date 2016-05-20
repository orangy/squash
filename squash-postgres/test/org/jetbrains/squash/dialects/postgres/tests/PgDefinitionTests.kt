package org.jetbrains.squash.dialects.postgres.tests

import org.jetbrains.squash.tests.*

class PgDefinitionTests : DefinitionTests(), DatabaseTests by PgDatabaseTests()