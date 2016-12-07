package org.jetbrains.squash.dialects.postgres

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.dialect.*

object PgDialect : BaseSQLDialect("Postgres") {
    override val definition: DefinitionSQLDialect = object : BaseDefinitionSQLDialect(this) {
        override fun columnTypeSQL(builder: SQLStatementBuilder, column: Column<*>) {
            if (column.hasProperty<AutoIncrementProperty>()) {
                require(column.type is IntColumnType || column.type is LongColumnType) {
                    "AutoIncrement column for '${column.type}' is not supported by $this"
                }
                builder.append("SERIAL")
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
                else -> super.columnTypeSQL(builder, type)
            }
        }
    }
}