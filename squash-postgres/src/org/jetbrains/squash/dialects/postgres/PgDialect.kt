package org.jetbrains.squash.dialects.postgres

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.dialect.*

object PgDialect : BaseSQLDialect("Postgres") {
    override val definition: DefinitionSQLDialect = object : BaseDefinitionSQLDialect(this) {
        override fun columnTypeSQL(builder: SQLStatementBuilder, column: Column<*>, properties: Set<ColumnProperty>) {
            when (column) {
                is AutoIncrementColumn -> {
                    require(column.column.type is IntColumnType || column.column.type is LongColumnType) {
                        "Autoincrement column for '${column.column.type}' is not supported by $this"
                    }
                    builder.append("SERIAL")
                }
                else -> super.columnTypeSQL(builder, column, properties)
            }
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