package org.jetbrains.squash.dialects.postgres.tests

import kotlinx.support.jdk7.*
import org.jetbrains.squash.connection.*
import org.jetbrains.squash.definition.*
import org.jetbrains.squash.dialects.postgres.*
import org.jetbrains.squash.tests.*

class PgDatabaseTests : DatabaseTests {
    override val idColumnType: String get() = "SERIAL"

    fun withConnection(block: (DatabaseConnection) -> Unit) {
        val connection = PgConnection.create("localhost:5432/", "postgres")
        block(connection)
    }

    override fun withTables(vararg tables: Table, statement: Transaction.() -> Unit) {
        withTransaction {
            databaseSchema().create(tables.toList())
            statement()
        }
    }

    override fun withTransaction(statement: Transaction.() -> Unit) {
        withConnection { connection ->
            connection.createTransaction().use {
                it.executeStatement("SET search_path TO pg_temp")
                it.statement()
            }
        }
    }
}