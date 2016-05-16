package org.jetbrains.squash.dialects.h2.tests

import kotlinx.support.jdk7.*
import org.jetbrains.squash.*
import org.jetbrains.squash.definition.*
import org.jetbrains.squash.dialect.*
import org.jetbrains.squash.dialects.h2.*
import kotlin.test.*

fun withTables(vararg tables: Table, statement: Transaction.() -> Unit) {
    H2Dialect.createMemoryConnection().use { connection ->
        val database = Database(connection, tables.toList())
        database.createSchema()
        connection.createTransaction().use(statement)
    }
}

fun withConnection(statement: Transaction.() -> Unit) {
    H2Dialect.createMemoryConnection().use { connection ->
        connection.createTransaction().use(statement)
    }
}

fun List<SQLStatement>.assertSQL(text: () -> String) {
    val sql = joinToString("\n") { it.sql }
    assertEquals(text().trimIndent(), sql)
}

fun SQLStatement.assertSQL(text: () -> String) {
    assertEquals(text().trimIndent(), sql)
}
