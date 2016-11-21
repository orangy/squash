package org.jetbrains.squash.dialects.postgres.tests

import com.opentable.db.postgres.embedded.*
import org.jetbrains.squash.dialects.postgres.*
import org.jetbrains.squash.tests.*

val pg = EmbeddedPostgres.builder().start()

class PgDatabaseTests : DatabaseTests {
    override val idColumnType: String get() = "SERIAL"
    override fun primaryKey(table: String, column: String): String = ", CONSTRAINT PK_$table PRIMARY KEY ($column)"
    override fun autoPrimaryKey(table: String, column: String): String = primaryKey(table, column)

    override fun createConnection() = PgConnection.create("localhost:${pg.port}/", "postgres")
    override fun createTransaction() = createConnection().createTransaction().apply {
        executeStatement("SET search_path TO pg_temp")
    }
}