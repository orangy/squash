package org.jetbrains.squash.dialects.postgres

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.dialect.*

object PostgresDialect : BaseSQLDialect("Postgres") {
    override val definition: DefinitionSQLDialect = object : BaseDefinitionSQLDialect(this) {
        override fun columnTypeSQL(builder: SQLBuilder, column: Column<*>, properties: Set<ColumnProperty>) {
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

        override fun columnTypeSQL(builder: SQLBuilder, type: ColumnType) {
            when (type) {
                is UUIDColumnType -> builder.append("bytea")
                is BlobColumnType -> builder.append("bytea")
                is BinaryColumnType -> builder.append("bytea")
                is DateTimeColumnType -> builder.append("TIMESTAMP")
                else -> super.columnTypeSQL(builder, type)
            }
        }
    }
}