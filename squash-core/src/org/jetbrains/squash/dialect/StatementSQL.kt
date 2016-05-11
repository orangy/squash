package org.jetbrains.squash.dialect

import org.jetbrains.squash.definition.*

data class StatementSQL(val sql: String, val indexes: Map<Column<*>, Int>) {
    operator fun plus(value: String) = StatementSQL(sql + value, indexes)
}