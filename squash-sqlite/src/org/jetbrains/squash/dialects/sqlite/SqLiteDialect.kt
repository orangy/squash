package org.jetbrains.squash.dialects.sqlite

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.dialect.*
import org.jetbrains.squash.query.*

object SqLiteDialect : BaseSQLDialect("SQLite") {
    override val definition: DefinitionSQLDialect = object : BaseDefinitionSQLDialect(this) {
        override fun columnTypeSQL(builder: SQLStatementBuilder, column: Column<*>, properties: Set<ColumnProperty>) {
            when (column) {
                is AutoIncrementColumn -> {
                    require(column.column.type is IntColumnType || column.column.type is LongColumnType) {
                        "Autoincrement column for '${column.column.type}' is not supported by $this"
                    }
                    require(BaseSQLDialect.ColumnProperty.NULLABLE !in properties) { "Column ${column.name} cannot be both AUTOINCREMENT and NULL" }
                    //columnTypeSQL(builder, column.column, properties + BaseSQLDialect.ColumnProperty.AUTOINCREMENT).toString()
                    builder.append("INTEGER NOT NULL PRIMARY KEY")
                }
                else -> super.columnTypeSQL(builder, column, properties)
            }
        }

        override fun primaryKeyDefinitionSQL(builder: SQLStatementBuilder, key: PrimaryKeyConstraint, table: Table): SQLStatementBuilder {
            if (key.columns.all { it is AutoIncrementColumn }) // ignore PK from autoincrement columns
                return builder
            return super.primaryKeyDefinitionSQL(builder, key, table)
        }

        override fun foreignKeys(table: Table): List<SQLStatement> {
            return listOf() // SqLite doesn't support FK in ALTER statement
        }

        override fun columnTypeSQL(builder: SQLStatementBuilder, type: ColumnType) {
            when (type) {
                else -> super.columnTypeSQL(builder, type)
            }
        }
    }

    override fun appendOrderExpression(builder: SQLStatementBuilder, order: QueryOrder) {
        // NULLS LAST
        appendExpression(builder, order.expression)
        builder.append(" IS NULL, ")

        // Main order
        appendExpression(builder, order.expression)
        when (order) {
            is QueryOrder.Ascending -> { /* ASC is default */
            }
            is QueryOrder.Descending -> builder.append(" DESC")
        }
    }
}