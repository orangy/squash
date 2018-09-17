package org.jetbrains.squash.dialects.postgres

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.dialect.BaseDefinitionSQLDialect
import org.jetbrains.squash.dialect.BaseSQLDialect
import org.jetbrains.squash.dialect.DefinitionSQLDialect
import org.jetbrains.squash.dialect.SQLStatementBuilder
import org.jetbrains.squash.expressions.Expression
import org.jetbrains.squash.expressions.ArrayInExpression
import org.jetbrains.squash.expressions.ArrayOverlapExpression

object PgDialect : BaseSQLDialect("Postgres") {
    override val definition: DefinitionSQLDialect = object : BaseDefinitionSQLDialect(this) {
        override fun columnTypeSQL(builder: SQLStatementBuilder, column: Column<*>) {
            if (column.hasProperty<AutoIncrementProperty>()) {
                require(!column.hasProperty<NullableProperty>()) { "Column ${column.name} cannot be both AUTOINCREMENT and NULL" }
                val type = column.type
                val autoincrement = when (type) {
                    is IntColumnType -> "SERIAL"
                    is LongColumnType -> "BIGSERIAL"
                    else -> error("AutoIncrement column for '$type' is not supported by $this")
                }
                builder.append(autoincrement)
            } else super.columnTypeSQL(builder, column)
        }

        override fun columnAutoIncrementProperty(builder: SQLStatementBuilder, property: AutoIncrementProperty?) {
            // do nothing, we already handled AutoIncrementProperty as SERIAL
        }

        override fun columnPropertiesSQL(builder: SQLStatementBuilder, column: Column<*>) {
            super.columnPropertiesSQL(builder, column)
        }

        override fun columnTypeSQL(builder: SQLStatementBuilder, type: ColumnType) {
            when (type) {
                is UUIDColumnType -> builder.append("UUID")
                is BlobColumnType -> builder.append("BYTEA")
                is BinaryColumnType -> builder.append("BYTEA")
                is DateTimeColumnType -> builder.append("TIMESTAMP")
                is OffsetDateTimeColumnType -> builder.append("TIMESTAMP WITH TIME ZONE")
                is IntArrayColumnType -> builder.append("INT[]")
                is TextArrayColumnType -> builder.append("TEXT[]")
                is JsonbColumnType -> builder.append("JSONB")
                else -> super.columnTypeSQL(builder, type)
            }
        }
    }

    override fun <T> appendExpression(builder: SQLStatementBuilder, expression: Expression<T>): Unit = with(builder) {
        when (expression) {
            is ArrayInExpression<*> -> {
                appendExpression(this, expression.value)
                append(" @> ARRAY[")
                expression.values.forEachIndexed { index, value ->
                    if (index > 0)
                        append(", ")
                    appendLiteralSQL(this, value)
                }
                append("]")
            }
            is ArrayOverlapExpression<*> -> {
                appendExpression(this, expression.value)
                append(" && ARRAY[")
                var anyValueIsString = false
                expression.values.forEachIndexed { index, value ->
                    if (index > 0)
                        append(", ")
                    appendLiteralSQL(this, value)
                    if (value is String)
                        anyValueIsString = true
                }
                append("]")
                if (anyValueIsString)
                    append("::text[]")
            }
            else -> super.appendExpression(builder, expression)
        }
    }
}