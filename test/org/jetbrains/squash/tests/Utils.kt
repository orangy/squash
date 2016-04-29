package org.jetbrains.squash.tests

import kotlinx.support.jdk7.*
import org.jetbrains.squash.*
import org.jetbrains.squash.dialects.*

fun withTables(vararg tables: Table, statement: Transaction.() -> Unit) {
    H2Dialect.createMemoryConnection().use { connection ->
        val database = Database(connection, tables.toList())
        database.createSchema()
        connection.createTransaction().use(statement)
    }
}

fun <T : Table> Transaction.exists(table: T): Boolean {
    return querySchema().tables().any { String.CASE_INSENSITIVE_ORDER.compare(it.name, table.tableName) == 0 }
}