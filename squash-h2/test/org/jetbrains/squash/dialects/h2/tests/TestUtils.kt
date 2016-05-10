package org.jetbrains.squash.dialects.h2.tests

import kotlinx.support.jdk7.*
import org.jetbrains.squash.*
import org.jetbrains.squash.definition.*
import org.jetbrains.squash.dialects.h2.*

fun withTables(vararg tables: Table, statement: Transaction.() -> Unit) {
    H2Dialect.createMemoryConnection().use { connection ->
        val database = Database(connection, tables.toList())
        database.createSchema()
        connection.createTransaction().use(statement)
    }
}
