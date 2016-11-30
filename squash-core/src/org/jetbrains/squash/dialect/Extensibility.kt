package org.jetbrains.squash.dialect

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.expressions.*

interface DialectExpression<out R> : Expression<R> {
    fun appendTo(builder: SQLStatementBuilder)
}

interface DialectColumn<out V> : Column<V> {
    fun appendTo(dialect: SQLStatementBuilder)
}