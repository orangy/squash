package org.jetbrains.squash.dialect

import org.jetbrains.squash.definition.*

interface DefinitionSQLDialect {
    fun tableSQL(table: TableDefinition): List<SQLStatement>
    fun foreignKeys(table: TableDefinition): List<SQLStatement>
}