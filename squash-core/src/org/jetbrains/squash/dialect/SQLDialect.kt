package org.jetbrains.squash.dialect

import org.jetbrains.squash.*
import org.jetbrains.squash.statements.*

interface SQLDialect {
    val definition: DefinitionSQLDialect

    fun nameSQL(name: Name): String
    fun idSQL(name: Name): String

    fun <T> statementSQL(statement: Statement<T>): SQLStatement
    fun literalSQL(value: Any?): SQLStatement
}

