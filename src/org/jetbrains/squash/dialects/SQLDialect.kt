package org.jetbrains.squash.dialects

import org.jetbrains.squash.*
import org.jetbrains.squash.statements.*

interface SQLDialect {
    fun tableDefinitionSQL(table: Table): String

    fun <T> statementSQL(statement: Statement<T>): StatementSQL

}

data class StatementSQL(val sql: String, val arguments: Map<Column<*>, Int>)

open class BaseSQLDialect : SQLDialect {
    override fun <T> statementSQL(statement: Statement<T>): StatementSQL = when (statement) {
        is InsertStatement<*, *> -> insertStatementSQL(statement)
        else -> error("Statement '$statement' is not supported by SQLDialect '$this'")
    }

    private fun insertStatementSQL(statement: InsertStatement<*, *>): StatementSQL {
        val arguments = mutableMapOf<Column<*>, Int>()
        val names = mutableListOf<String>()
        val values = mutableListOf<Any?>()
        var index = 0
        for ((column, value) in statement.values) {
            names.add(column.name)
            values.add("?")
            arguments[column] = index++
        }
        val sql = "INSERT INTO ${statement.table} (${names.joinToString()}) VALUES (${values.joinToString()})"
        return StatementSQL(sql, arguments)
    }

    override fun tableDefinitionSQL(table: Table): String = buildString {
        append("CREATE TABLE IF NOT EXISTS ${table.tableName}")
        if (table.tableColumns.any()) {
            append(" (")
            append(table.tableColumns.map { columnDefinitionSQL(it) }.joinToString())
            val primaryKeys = table.tableColumns.filterIsInstance<PrimaryKeyColumn<*>>()

            if (primaryKeys.any()) {
                append(", ")
                primaryKeyDefinitionSQL(primaryKeys, table)
            } else {
                val autoIncrement = table.tableColumns.filterIsInstance<AutoIncrementColumn<*>>()
                if (autoIncrement.any()) {
                    append(", ")
                    primaryKeyDefinitionSQL(autoIncrement, table)
                }
            }
/*
            var pkey = table.columns.filter { it.indexInPK != null }.sortedBy { it.indexInPK }

            if (pkey.isEmpty()) {
                pkey = table.columns.filter { it.columnType.autoinc }
            }

            if (pkey.isNotEmpty()) {
                append(pkey.joinToString(
                        prefix = ", CONSTRAINT ${Transaction.current().quoteIfNecessary("pk_$tableName")} PRIMARY KEY (", postfix = ")") {
                    Transaction.current().identity(it)
                })
            }
*/
            append(")")
        }
    }

    private fun StringBuilder.primaryKeyDefinitionSQL(primaryKeys: List<Column<*>>, table: Table) {
        append("CONSTRAINT pk_${table.tableName} PRIMARY KEY (")
        append(primaryKeys.map { it.name }.joinToString())
        append(")")
    }

    protected open fun columnDefinitionSQL(column: Column<*>): String = buildString {
        append(column.name)
        append(" ")
        append(columnTypeSQL(column, emptySet()))
    }

    enum class ColumnProperty {
        NULLABLE, AUTOINCREMENT, DEFAULT
    }

    protected open fun literalSQL(value: Any?) = when (value) {
        null -> "NULL"
        is String -> "'$value'"
        else -> value
    }

    protected open fun columnTypeSQL(column: Column<*>, properties: Set<ColumnProperty>): String = when (column) {
        is TableColumn -> {
            if (ColumnProperty.NULLABLE in properties)
                "${columnTypeSQL(column.type)} NULL"
            else
                "${columnTypeSQL(column.type)} NOT NULL"
        }

        is NullableColumn -> {
            require(ColumnProperty.AUTOINCREMENT !in properties) { "Column ${column.name} cannot be both AUTOINCREMENT and NULL" }
            columnTypeSQL(column.column, properties + ColumnProperty.NULLABLE)
        }

        is AutoIncrementColumn -> {
            require(ColumnProperty.NULLABLE !in properties) { "Column ${column.name} cannot be both AUTOINCREMENT and NULL" }
            "${columnTypeSQL(column.column, properties + ColumnProperty.AUTOINCREMENT)} AUTO_INCREMENT"
        }

        is PrimaryKeyColumn -> columnTypeSQL(column.column, properties)
        is DefaultValueColumn<*> -> "${columnTypeSQL(column.column, properties + ColumnProperty.DEFAULT)} DEFAULT ${literalSQL(column.value)}"

        else -> error("Column class '${column.javaClass.simpleName}' is not supported by SQLDialect '$this'")
    }

    protected open fun columnTypeSQL(type: ColumnType): String = when (type) {
        is ReferenceColumnType<*> -> columnTypeSQL(type.column.type)
        is CharacterColumnType -> "CHAR"
        is LongColumnType -> "BIGINT"
        is IntegerColumnType -> "INT"
        is DecimalColumnType -> "DECIMAL(${type.scale}, ${type.precision})"
        is EnumerationColumnType<*> -> "INT"
        is DateColumnType -> "DATE"
        is DateTimeColumnType -> "DATETIME"
        is BinaryColumnType -> "VARBINARY(${type.length})"
        is UUIDColumnType -> "BINARY(16)"
        is StringColumnType -> {
            val sqlType = when (type.length) {
                in 1..255 -> "VARCHAR(${type.length})"
                else -> "TEXT"
            }
            if (type.collate == null)
                sqlType
            else
                sqlType + " COLLATE ${type.collate}"
        }
        else -> error("Column type '$type' is not supported by SQLDialect '$this'")
    }

    override fun toString(): String = javaClass.simpleName
}

