package org.jetbrains.squash.dialect

import org.jetbrains.squash.definition.*

open class BaseDefinitionSQLDialect(val dialect: SQLDialect) : DefinitionSQLDialect {
    override fun tableSQL(table: Table): String = buildString {
        append("CREATE TABLE IF NOT EXISTS ${dialect.nameSQL(table.tableName)}")
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
            append(")")
        }
    }

    protected open fun StringBuilder.primaryKeyDefinitionSQL(primaryKeys: List<Column<*>>, table: Table) {
        append("CONSTRAINT pk_${dialect.nameSQL(table.tableName)} PRIMARY KEY (")
        append(primaryKeys.map { it.name.id }.joinToString())
        append(")")
    }

    protected open fun columnDefinitionSQL(column: Column<*>): String = buildString {
        append(column.name.id)
        append(" ")
        append(columnTypeSQL(column, emptySet()))
    }

    protected open fun columnTypeSQL(column: Column<*>, properties: Set<BaseSQLDialect.ColumnProperty>): String = when (column) {
        is DataColumn -> {
            if (BaseSQLDialect.ColumnProperty.NULLABLE in properties)
                "${columnTypeSQL(column.type)} NULL"
            else
                "${columnTypeSQL(column.type)} NOT NULL"
        }

        is NullableColumn -> {
            require(BaseSQLDialect.ColumnProperty.AUTOINCREMENT !in properties) { "Column ${column.name} cannot be both AUTOINCREMENT and NULL" }
            columnTypeSQL(column.column, properties + BaseSQLDialect.ColumnProperty.NULLABLE)
        }

        is AutoIncrementColumn -> {
            require(BaseSQLDialect.ColumnProperty.NULLABLE !in properties) { "Column ${column.name} cannot be both AUTOINCREMENT and NULL" }
            "${columnTypeSQL(column.column, properties + BaseSQLDialect.ColumnProperty.AUTOINCREMENT)} AUTO_INCREMENT"
        }

        is PrimaryKeyColumn -> columnTypeSQL(column.column, properties)
        is DefaultValueColumn<*> -> "${columnTypeSQL(column.column, properties + BaseSQLDialect.ColumnProperty.DEFAULT)} DEFAULT ${dialect.literalSQL(column.value)}"

        else -> error("Column class '${column.javaClass.simpleName}' is not supported by $this")
    }

    protected open fun columnTypeSQL(type: ColumnType): String = when (type) {
        is ReferenceColumnType<*> -> columnTypeSQL(type.column.type)
        is CharColumnType -> "CHAR"
        is LongColumnType -> "BIGINT"
        is IntColumnType -> "INT"
        is DecimalColumnType -> "DECIMAL(${type.scale}, ${type.precision})"
        is EnumColumnType<*> -> "INT"
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
        else -> error("Column type '$type' is not supported by $this")
    }
}