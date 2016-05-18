package org.jetbrains.squash.dialects.postgres.tests

import kotlinx.support.jdk7.*
import org.jetbrains.squash.*
import org.jetbrains.squash.definition.*
import org.jetbrains.squash.dialects.postgres.*
import org.jetbrains.squash.tests.*

class PgDatabaseTests : DatabaseTests {
    fun withConnection(block: (DatabaseConnection) -> Unit) {
        val connection = PgConnection.create("jdbc:postgresql://localhost:5432/", "org.postgresql.Driver", "postgres")
        block(connection)
    }

    override fun withTables(vararg tables: Table, statement: Transaction.() -> Unit) {
        withTransaction {
            val database = Database(connection, tables.toList())
            database.createSchema(this)
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