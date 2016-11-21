package org.jetbrains.squash.dialects.sqlite.tests

import org.jetbrains.squash.connection.*
import org.jetbrains.squash.dialects.sqlite.*
import org.jetbrains.squash.tests.*

class SqLiteDatabaseTests : DatabaseTests {
    override val idColumnType: String = "INTEGER NOT NULL PRIMARY KEY"
    override fun primaryKey(table: String, column: String): String = ", CONSTRAINT PK_$table PRIMARY KEY ($column)"
    override fun autoPrimaryKey(table: String, column: String) = ""

    override fun createConnection(): DatabaseConnection = SqLiteConnection.createMemoryConnection()
}