package org.jetbrains.squash.dialects.sqlite.tests

import kotlinx.support.jdk7.*
import org.jetbrains.squash.connection.*
import org.jetbrains.squash.definition.*
import org.jetbrains.squash.dialects.sqlite.*
import org.jetbrains.squash.tests.*

class SqLiteDatabaseTests : DatabaseTests {
    override val idColumnType: String = "INTEGER NOT NULL PRIMARY KEY"
    override fun primaryKey(table: String, column: String): String = ", CONSTRAINT PK_$table PRIMARY KEY ($column)"
    override fun autoPrimaryKey(table: String, column: String) = ""

    fun <R> withConnection(block: (DatabaseConnection) -> R): R {
        val connection = SqLiteConnection.createMemoryConnection()
        return block(connection)
    }

    override fun <R> withTables(vararg tables: Table, statement: Transaction.() -> R): R {
        return withTransaction {
            databaseSchema().create(tables.toList())
            statement()
        }
    }

    override fun <R> withTransaction(statement: Transaction.() -> R): R {
        return withConnection { connection ->
            connection.createTransaction().use {
                it.statement()
            }
        }
    }
}