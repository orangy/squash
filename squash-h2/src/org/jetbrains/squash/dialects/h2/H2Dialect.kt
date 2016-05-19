package org.jetbrains.squash.dialects.h2

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.dialect.*

object H2Dialect : BaseSQLDialect("H2") {
    override val definition: DefinitionSQLDialect = object : BaseDefinitionSQLDialect(this) {
        override fun columnTypeSQL(builder: SQLBuilder, type: ColumnType): Unit {
            when (type) {
                is UUIDColumnType -> builder.append("UUID")
                else -> super.columnTypeSQL(builder, type)
            }
        }
    }
}
