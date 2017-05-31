package org.jetbrains.squash.dialects.mysql.tests

import org.jetbrains.squash.tests.*

class MySqlSchemaTests : SchemaTests(), DatabaseTests by MySqlDatabaseTests()