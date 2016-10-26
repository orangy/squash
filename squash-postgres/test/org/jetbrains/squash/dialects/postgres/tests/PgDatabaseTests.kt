package org.jetbrains.squash.dialects.postgres.tests

import com.opentable.db.postgres.embedded.*
import kotlinx.support.jdk7.*
import org.jetbrains.squash.connection.*
import org.jetbrains.squash.definition.*
import org.jetbrains.squash.dialects.postgres.*
import org.jetbrains.squash.tests.*

val pg = EmbeddedPostgres.builder().start()

class PgDatabaseTests : DatabaseTests {
    override val idColumnType: String get() = "SERIAL"
    override fun primaryKey(table: String, column: String): String = ", CONSTRAINT PK_$table PRIMARY KEY ($column)"
    override fun autoPrimaryKey(table: String, column: String): String = primaryKey(table, column)

    fun <R> withConnection(block: (DatabaseConnection) -> R): R {
        val connection = PgConnection.create("localhost:${pg.port}/", "postgres")
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
                it.executeStatement("SET search_path TO pg_temp")
                it.statement()
            }
        }
    }
}