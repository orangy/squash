package org.jetbrains.squash

import kotlinx.support.jdk7.*
import org.jetbrains.squash.definition.*
import org.jetbrains.squash.dialect.*
import java.util.*

class Database(val connection: DatabaseConnection, val tables: List<Table>) {

    data class DatabaseSchemaValidationItem(val message: String)

    fun validateSchema(transaction: Transaction): List<DatabaseSchemaValidationItem> {
        val tableMap = tables.associateBy { it.tableName.id.toLowerCase() }
        val validationResult = mutableListOf<DatabaseSchemaValidationItem>()
        transaction.querySchema().tables().forEach { tableSchema ->
            val tableDefinition = tableMap[tableSchema.name.toLowerCase()]
            if (tableDefinition == null)
                validationResult.add(DatabaseSchemaValidationItem("Table definition not found for schema table '$tableSchema"))
            else {
                val columnsSchema = tableSchema.columns().associateBy { it.name.toLowerCase() }
                val columnsDefinition = tableDefinition.tableColumns.associateBy { it.name.id.toLowerCase() }
                val allNames = columnsDefinition.keys + columnsSchema.keys
                for (name in allNames) {
                    val columnSchema = columnsSchema[name]
                    val columnDefinition = columnsDefinition[name]
                    when {
                        columnSchema == null -> validationResult.add(DatabaseSchemaValidationItem("Column schema not found for definition column '$columnDefinition in table '$tableSchema'"))
                        columnDefinition == null -> validationResult.add(DatabaseSchemaValidationItem("Column definition not found for schema column '$columnSchema' in table '$tableDefinition'"))
                        else -> {

                        }
                    }
                }
            }
        }
        return validationResult
    }

    fun createSchema(transaction: Transaction) {
        val statements = transaction.createSchemaStatements(tables)
        for (statement in statements) {
            transaction.executeStatement(statement)
        }
    }

    fun Transaction.createSchemaStatements(tables: List<Table>): List<SQLStatement> {
        val statements = ArrayList<SQLStatement>()
        if (tables.isEmpty())
            return statements

        val existingTables = querySchema().tables().toList()
        for (table in tables) {
            if (existingTables.any { it.name == table.tableName.id })
                continue

            val tableDefinition = connection.dialect.definition.tableSQL(table)
            statements.addAll(tableDefinition)
        }
        return statements
    }

}

