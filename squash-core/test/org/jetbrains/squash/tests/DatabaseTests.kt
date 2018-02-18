package org.jetbrains.squash.tests

import org.jetbrains.squash.connection.*
import org.jetbrains.squash.definition.*
import org.jetbrains.squash.dialect.*
import kotlin.test.*

interface DatabaseTests {
    val blobType: String
    val quote: String
    val indexIfNotExists : String
    fun getIdColumnType(columnType: ColumnType): String
    fun primaryKey(name: String, vararg column: String): String
    fun autoPrimaryKey(table: String, column: String): String

    fun createConnection(): DatabaseConnection
    fun createTransaction(): Transaction = createConnection().createTransaction()

    fun <R> withTables(vararg tables: TableDefinition, statement: Transaction.() -> R): R = withTransaction {
        databaseSchema().create(tables.toList())
        statement()
    }

    fun <R> withTransaction(statement: Transaction.() -> R): R = createTransaction().use(statement)

    fun List<SQLStatement>.assertSQL(text: () -> String) {
        val sql = joinToString("\n") { it.sql }
        assertEquals(text().trimIndent(), sql)
    }

    fun List<SQLStatement>.assertSQL(text: String) {
        val sql = joinToString("\n") { it.sql }
        assertEquals(text.trimIndent(), sql)
    }

    fun SQLStatement.assertSQL(text: () -> String) {
        assertEquals(text().trimIndent(), sql)
    }
}