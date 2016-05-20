package org.jetbrains.squash.dialects.postgres.tests

import org.jetbrains.squash.tests.*

class PgSchemaTests : SchemaTests(), DatabaseTests by PgDatabaseTests()