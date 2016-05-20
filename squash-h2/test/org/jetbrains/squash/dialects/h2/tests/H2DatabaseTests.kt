package org.jetbrains.squash.dialects.h2.tests

import kotlinx.support.jdk7.*
import org.jetbrains.squash.connection.*
import org.jetbrains.squash.definition.*
import org.jetbrains.squash.dialects.h2.*
import org.jetbrains.squash.tests.*

class H2DatabaseTests : DatabaseTests {
    override val idColumnType: String get() = "INT NOT NULL AUTO_INCREMENT"

    fun withConnection(block: (DatabaseConnection) -> Unit) {
        H2Connection.createMemoryConnection().use(block)
    }

    override fun withTables(vararg tables: Table, statement: Transaction.() -> Unit) {
        withConnection { connection ->
            connection.createTransaction().use { transaction ->
                transaction.databaseSchema().create(tables.toList(), transaction)
                transaction.statement()
            }
        }
    }

    override fun withTransaction(statement: Transaction.() -> Unit) {
        withConnection { connection ->
            connection.createTransaction().use(statement)
        }
    }
}