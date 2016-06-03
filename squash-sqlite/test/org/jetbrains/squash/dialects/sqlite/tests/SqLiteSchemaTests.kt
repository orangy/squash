package org.jetbrains.squash.dialects.sqlite.tests

import org.jetbrains.squash.tests.*

class SqLiteSchemaTests : SchemaTests(), DatabaseTests by SqLiteDatabaseTests() {
}