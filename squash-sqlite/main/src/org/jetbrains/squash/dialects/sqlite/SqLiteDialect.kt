package org.jetbrains.squash.dialects.sqlite

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.dialect.*
import org.jetbrains.squash.query.*

object SqLiteDialect : BaseSQLDialect("SQLite") {
    override val definition: DefinitionSQLDialect = object : BaseDefinitionSQLDialect(this) {
        override fun columnTypeSQL(builder: SQLStatementBuilder, column: Column<*>) {
            if (column.hasProperty<AutoIncrementProperty>()) {
                require(!column.hasProperty<NullableProperty>()) { "Column ${column.name} cannot be both AUTOINCREMENT and NULL" }
                val type = column.type
                val autoincrement = when (type) {
                    is IntColumnType -> "INTEGER"
                    is LongColumnType -> "INTEGER" // it's always RowID anyway
                    else -> error("AutoIncrement column for '$type' is not supported by $this")
                }
                builder.append("$autoincrement PRIMARY KEY")
            } else super.columnTypeSQL(builder, column)
        }

        override fun columnAutoIncrementProperty(builder: SQLStatementBuilder, property: AutoIncrementProperty?) {
            // do nothing, we already handled AutoIncrementProperty as automatic rowid mapping
        }

        override fun primaryKeyDefinitionSQL(builder: SQLStatementBuilder, key: PrimaryKeyConstraint, table: Table): SQLStatementBuilder {
            if (key.columns.all { it.hasProperty<AutoIncrementProperty>() }) // ignore PK from autoincrement columns
                return builder
            return super.primaryKeyDefinitionSQL(builder, key, table)
        }

        override fun foreignKeys(table: TableDefinition): List<SQLStatement> {
            return listOf() // SqLite doesn't support FK in ALTER statement
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