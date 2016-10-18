package org.jetbrains.squash.dialects.h2.tests

import kotlinx.support.jdk7.*
import org.jetbrains.squash.connection.*
import org.jetbrains.squash.definition.*
import org.jetbrains.squash.dialects.h2.*
import org.jetbrains.squash.tests.*

class H2DatabaseTests : DatabaseTests {
    override val idColumnType: String get() = "INT NOT NULL AUTO_INCREMENT"
    override fun primaryKey(table: String, column: String): String = ", CONSTRAINT PK_$table PRIMARY KEY ($column)"
    override fun autoPrimaryKey(table: String, column: String): String = primaryKey(table, column)

    fun <R> withConnection(block: (DatabaseConnection) -> R): R {
        val connection = H2Connection.createMemoryConnection()
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
            connection.createTransaction().use(statement)
        }
    }
}