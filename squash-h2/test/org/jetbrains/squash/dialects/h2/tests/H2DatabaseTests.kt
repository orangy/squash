package org.jetbrains.squash.dialects.h2.tests

import org.jetbrains.squash.connection.*
import org.jetbrains.squash.dialects.h2.*
import org.jetbrains.squash.tests.*

class H2DatabaseTests : DatabaseTests {
    override val idColumnType: String get() = "INT NOT NULL AUTO_INCREMENT"
    override fun primaryKey(table: String, column: String): String = ", CONSTRAINT PK_$table PRIMARY KEY ($column)"
    override fun autoPrimaryKey(table: String, column: String): String = primaryKey(table, column)

    override fun createConnection(): DatabaseConnection = H2Connection.createMemoryConnection()
}