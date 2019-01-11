package org.jetbrains.squash.dialect

data class SQLStatement(val sql: String, val arguments: List<SQLArgument>) {
    operator fun plus(value: String) = SQLStatement(sql + value, arguments)

    override fun toString(): String {
        val args = if (arguments.any()) {
            arguments.joinToString(", ", prefix = "[?= ", postfix = "] ") { it.value.toString() }
        } else ""
        return "[SQL] \"$sql\" $args"
    }
}

