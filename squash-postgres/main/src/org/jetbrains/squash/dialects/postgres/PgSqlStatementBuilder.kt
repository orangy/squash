package org.jetbrains.squash.dialects.postgres

import org.jetbrains.squash.dialect.*

class PgSqlStatementBuilder : SQLStatementBuilder() {
    override fun <V> appendArgument(value: V) {
        val index = arguments.size
        append("?")
        //append("\$${index}")
        arguments.add(SQLArgument(index, value))
    }
}
