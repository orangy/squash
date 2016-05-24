package org.jetbrains.squash.schema

import org.jetbrains.squash.dialect.*

/**
 * Provides facilities for querying and modifying database schema
 */
interface DatabaseSchema {
    fun tables(): Sequence<SchemaTable>

    fun create(tables: List<org.jetbrains.squash.definition.Table>)
    fun createStatements(tables: List<org.jetbrains.squash.definition.Table>): List<SQLStatement>

    data class DatabaseSchemaValidationItem(val message: String)

    fun validate(tables: List<org.jetbrains.squash.definition.Table>): List<DatabaseSchemaValidationItem>

    interface SchemaTable {
        val name: String
        open fun columns(): Sequence<SchemaColumn>
    }

    interface SchemaColumn {
        val name: String
        val size: Int
        val nullable: Boolean
        val autoIncrement: Boolean
    }
}
