package org.jetbrains.squash.dialects.postgres

import org.jetbrains.squash.*

object PostgresDialect : BaseSQLDialect("Postgres") {
    override fun columnTypeSQL(column: Column<*>, properties: Set<ColumnProperty>): String = when (column) {
        is AutoIncrementColumn -> {
            require(column.column.type is IntColumnType || column.column.type is LongColumnType) {
                "Autoincrement column for '${column.column.type}' is not supported by $this"
            }
            "SERIAL"
        }
        else -> super.columnTypeSQL(column, properties)
    }

    override fun columnTypeSQL(type: ColumnType): String = when (type) {
        is UUIDColumnType -> "bytea"
        is BlobColumnType -> "bytea"
        is BinaryColumnType -> "bytea"
        is DateTimeColumnType -> "TIMESTAMP"
        else -> super.columnTypeSQL(type)
    }
}