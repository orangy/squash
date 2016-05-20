package org.jetbrains.squash.dialect

data class SQLStatement(val sql: String, val arguments: List<SQLArgument<*>>) {
    operator fun plus(value: String) = SQLStatement(sql + value, arguments)
}

