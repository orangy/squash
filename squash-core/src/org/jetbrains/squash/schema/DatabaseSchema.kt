package org.jetbrains.squash.schema

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.dialect.*

/**
 * Provides facilities for querying and modifying database schema
 */
interface DatabaseSchema {
    suspend fun tables(): Sequence<SchemaTable>

    suspend fun create(tables: List<TableDefinition>)
    suspend fun createStatements(tables: List<TableDefinition>): List<SQLStatement>

    data class DatabaseSchemaValidationItem(val message: String)

    suspend fun validate(tables: List<org.jetbrains.squash.definition.Table>): List<DatabaseSchemaValidationItem>

    interface SchemaTable {
        val name: String
        fun columns(): Sequence<SchemaColumn>
    }

    interface SchemaColumn {
        val name: String
        val nullable: Boolean
    }
}

suspend fun DatabaseSchema.create(vararg tables: TableDefinition) = create(tables.asList())