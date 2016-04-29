package org.jetbrains.squash.dialects

import org.jetbrains.squash.*

interface SQLDialect {
    fun tableDefinitionSQL(table: Table): String

}

abstract class BaseSQLDialect : SQLDialect {
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
                append(", ")
                primaryKeyDefinitionSQL(autoIncrement, table)
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
        NULLABLE, AUTOINCREMENT
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

        else -> error("Column class '${column.javaClass.simpleName}' is not supported by SQLDialect '${this}'")
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
        else -> error("Column type '$type' is not supported by SQLDialect '${this}'")
    }

    override fun toString(): String = javaClass.simpleName
}

