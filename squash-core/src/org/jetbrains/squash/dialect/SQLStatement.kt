package org.jetbrains.squash.dialect

import org.jetbrains.squash.definition.*

data class SQLStatement(val sql: String, val arguments: List<SQLArgument<*>>) {
    operator fun plus(value: String) = SQLStatement(sql + value, arguments)
}

class SQLBuilder() : Appendable {
    val stringBuilder = StringBuilder()
    val arguments = mutableListOf<SQLArgument<*>>()

    fun <V> appendArgument(column: Column<V>, value: V) = appendArgument(column.type, value)
    fun <V> appendArgument(argument: SQLArgument<V>) = appendArgument(argument.columnType, argument.value)

    fun <V> appendArgument(columnType: ColumnType, value: V) {
        val index = arguments.size
        arguments.add(SQLArgument(columnType, index, value))
    }

    fun append(sql: SQLStatement): SQLBuilder {
        append(sql.sql)
        sql.arguments.forEach {
            appendArgument(it)
        }
        return this
    }

    override fun append(csq: CharSequence): SQLBuilder {
        stringBuilder.append(csq)
        return this
    }

    override fun append(csq: CharSequence, start: Int, end: Int): SQLBuilder {
        stringBuilder.append(csq, start, end)
        return this
    }

    override fun append(c: Char): SQLBuilder {
        stringBuilder.append(c)
        return this
    }

    fun build(): SQLStatement {
        val statementSQL = SQLStatement(stringBuilder.toString(), arguments)
        return statementSQL
    }
}

class SQLArgument<V>(val columnType: ColumnType, val index: Int, val value: V)
