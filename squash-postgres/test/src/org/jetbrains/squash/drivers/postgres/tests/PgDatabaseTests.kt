package org.jetbrains.squash.drivers.postgres.tests

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.tests.*
import kotlin.test.*

abstract class PgDatabaseTests : DatabaseTests {
    override val indexIfNotExists: String = " IF NOT EXISTS"
    override val quote = "\""
    override val blobType = "BYTEA"
    override fun getIdColumnType(columnType: ColumnType): String = when (columnType) {
        is IntColumnType -> "SERIAL NOT NULL"
        is LongColumnType -> "BIGSERIAL NOT NULL"
        else -> fail("Unsupported column type $columnType")
    }

    override fun primaryKey(name: String, vararg column: String): String =
        ", CONSTRAINT PK_$name PRIMARY KEY (${column.joinToString()})"

    override fun autoPrimaryKey(table: String, column: String): String = primaryKey(table, column)

    override suspend fun createTransaction() = createConnection().createTransaction().apply {
        executeStatement("SET search_path TO pg_temp")
    }
}