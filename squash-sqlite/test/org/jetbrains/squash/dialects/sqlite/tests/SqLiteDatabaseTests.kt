package org.jetbrains.squash.dialects.sqlite.tests

import org.jetbrains.squash.connection.*
import org.jetbrains.squash.definition.*
import org.jetbrains.squash.dialects.sqlite.*
import org.jetbrains.squash.tests.*
import kotlin.test.*

class SqLiteDatabaseTests : DatabaseTests {
    override val indexIfNotExists: String = " IF NOT EXISTS"
    override val quote = "\""
    override val blobType = "BLOB"

    override fun getIdColumnType(columnType: ColumnType): String = when (columnType) {
        is IntColumnType -> "INTEGER PRIMARY KEY NOT NULL"
        is LongColumnType -> "INTEGER PRIMARY KEY NOT NULL"
        else -> fail("Unsupported column type $columnType")
    }

    override fun primaryKey(name: String, vararg column: String) = ", CONSTRAINT PK_$name PRIMARY KEY (${column.joinToString()})"

    override fun autoPrimaryKey(table: String, column: String) = ""

    override fun createConnection(): DatabaseConnection = SqLiteConnection.createMemoryConnection()
}