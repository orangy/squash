package org.jetbrains.squash.dialect

import org.jetbrains.squash.definition.*

open class BaseDefinitionSQLDialect(val dialect: SQLDialect) : DefinitionSQLDialect {
    override fun tableSQL(table: Table): SQLStatement = SQLBuilder().apply {
        append("CREATE TABLE IF NOT EXISTS ${dialect.nameSQL(table.tableName)}")
        if (table.tableColumns.any()) {
            append(" (")
            table.tableColumns.forEachIndexed { index, column ->
                if (index > 0)
                    append(", ")
                columnDefinitionSQL(this, column)
            }
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
    }.build()

    protected open fun SQLBuilder.primaryKeyDefinitionSQL(primaryKeys: List<Column<*>>, table: Table) {
        append("CONSTRAINT pk_${dialect.nameSQL(table.tableName)} PRIMARY KEY (")
        append(primaryKeys.map { it.name.id }.joinToString())
        append(")")
    }

    protected open fun columnDefinitionSQL(builder: SQLBuilder, column: Column<*>): Unit = with(builder) {
        append(column.name.id)
        append(" ")
        columnTypeSQL(this, column, emptySet())
    }

    protected open fun columnTypeSQL(builder: SQLBuilder, column: Column<*>, properties: Set<BaseSQLDialect.ColumnProperty>): Unit = with(builder) {
        when (column) {
            is DataColumn -> {
                if (BaseSQLDialect.ColumnProperty.NULLABLE in properties) {
                    columnTypeSQL(this, column.type)
                    append(" NULL")
                } else {
                    columnTypeSQL(this, column.type)
                    append(" NOT NULL")
                }
            }

            is NullableColumn -> {
                require(BaseSQLDialect.ColumnProperty.AUTOINCREMENT !in properties) { "Column ${column.name} cannot be both AUTOINCREMENT and NULL" }
                columnTypeSQL(this, column.column, properties + BaseSQLDialect.ColumnProperty.NULLABLE)
            }

            is AutoIncrementColumn -> {
                require(BaseSQLDialect.ColumnProperty.NULLABLE !in properties) { "Column ${column.name} cannot be both AUTOINCREMENT and NULL" }
                columnTypeSQL(this, column.column, properties + BaseSQLDialect.ColumnProperty.AUTOINCREMENT).toString()
                append(" AUTO_INCREMENT")
            }

            is PrimaryKeyColumn -> columnTypeSQL(this, column.column, properties)
            is DefaultValueColumn<*> -> {
                val literalSQL = dialect.literalSQL(column.value)
                columnTypeSQL(this, column.column, properties + BaseSQLDialect.ColumnProperty.DEFAULT).toString()
                append(" DEFAULT ")
                append(literalSQL)
            }

            else -> error("Column class '${column.javaClass.simpleName}' is not supported by $this")
        }
    }

    protected open fun columnTypeSQL(builder: SQLBuilder, type: ColumnType): Unit = with(builder) {
        when (type) {
            is ReferenceColumnType<*> -> columnTypeSQL(this, type.column.type)
            is CharColumnType -> append("CHAR")
            is LongColumnType -> append("BIGINT")
            is IntColumnType -> append("INT")
            is DecimalColumnType -> append("DECIMAL(${type.scale}, ${type.precision})")
            is EnumColumnType<*> -> append("INT")
            is DateColumnType -> append("DATE")
            is DateTimeColumnType -> append("DATETIME")
            is BinaryColumnType -> append("VARBINARY(${type.length})")
            is UUIDColumnType -> append("BINARY(16)")
            is StringColumnType -> {
                val sqlType = when (type.length) {
                    in 1..255 -> "VARCHAR(${type.length})"
                    else -> "TEXT"
                }
                if (type.collate == null)
                    append(sqlType)
                else
                    append(sqlType + " COLLATE ${type.collate}")
            }
            else -> error("Column type '$type' is not supported by $this")
        }
    }
}