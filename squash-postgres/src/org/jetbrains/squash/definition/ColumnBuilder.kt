package org.jetbrains.squash.definition

import java.time.OffsetDateTime

/**
 * Creates a [OffsetDateTime] column
 */
fun TableDefinition.offsetdatetime(name: String): ColumnDefinition<OffsetDateTime> {
    return createColumn(name, OffsetDateTimeColumnType)
}
