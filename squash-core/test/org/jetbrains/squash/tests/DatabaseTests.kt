package org.jetbrains.squash.tests

import org.jetbrains.squash.*
import org.jetbrains.squash.definition.*
import org.jetbrains.squash.dialect.*
import kotlin.test.*

interface DatabaseTests {
    fun withTables(vararg tables: Table, statement: Transaction.() -> Unit)
    fun withTransaction(statement: Transaction.() -> Unit)

    fun List<SQLStatement>.assertSQL(text: () -> String) {
        val sql = joinToString("\n") { it.sql }
        assertEquals(text().trimIndent(), sql)
    }

    fun List<SQLStatement>.assertSQL(text: String) {
        val sql = joinToString("\n") { it.sql }
        assertEquals(text.trimIndent(), sql)
    }

    fun SQLStatement.assertSQL(text: () -> String) {
        assertEquals(text().trimIndent(), sql)
    }
}