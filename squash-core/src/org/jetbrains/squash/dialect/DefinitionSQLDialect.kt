package org.jetbrains.squash.dialect

import org.jetbrains.squash.definition.*

interface DefinitionSQLDialect {
    fun tableSQL(table: Table): SQLStatement
    fun indicesSQL(table: Table): List<SQLStatement>
}