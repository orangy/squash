package org.jetbrains.squash.dialect

import org.jetbrains.squash.definition.*

interface DefinitionSQLDialect {
    fun tableSQL(table: Table): List<SQLStatement>
}