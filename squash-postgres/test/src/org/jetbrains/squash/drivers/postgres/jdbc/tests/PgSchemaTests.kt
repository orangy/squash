package org.jetbrains.squash.drivers.postgres.jdbc.tests

import org.jetbrains.squash.tests.*

class PgSchemaTests : SchemaTests(), DatabaseTests by PgDatabaseTests()