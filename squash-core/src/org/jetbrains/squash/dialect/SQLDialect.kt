package org.jetbrains.squash.dialect

import org.jetbrains.squash.*
import org.jetbrains.squash.definition.*
import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.statements.*

interface SQLDialect {
    val definition: DefinitionSQLDialect

    fun <T> statementSQL(statement: Statement<T>): StatementSQL

    fun querySQL(query: Query): StatementSQL

    fun <T> expressionSQL(expression: Expression<T>): String

    fun nameSQL(name: Name): String
    fun literalSQL(value: Any?): String
}

