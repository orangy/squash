package org.jetbrains.squash.dialects.h2.tests

import org.jetbrains.squash.connection.*
import org.jetbrains.squash.definition.*
import org.jetbrains.squash.dialects.h2.*
import org.jetbrains.squash.tests.*
import kotlin.test.*

class H2DatabaseTests : DatabaseTests {
    override val indexIfNotExists: String = " IF NOT EXISTS"
    override val quote = "\""
    override val blobType = "BLOB"
    override fun getIdColumnType(columnType: ColumnType): String = when (columnType) {
        is IntColumnType -> "INT NOT NULL AUTO_INCREMENT"
        is LongColumnType -> "BIGINT NOT NULL AUTO_INCREMENT"
        else -> fail("Unsupported column type $columnType")
    }

    override fun primaryKey(name: String, vararg column: String): String = ", CONSTRAINT PK_$name PRIMARY KEY (${column.joinToString()})"
    override fun autoPrimaryKey(table: String, column: String): String = primaryKey(table, column)

    override fun createConnection(): DatabaseConnection = H2Connection.createMemoryConnection()
}