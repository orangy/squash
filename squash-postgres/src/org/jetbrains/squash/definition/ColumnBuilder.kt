package org.jetbrains.squash.definition

import java.time.OffsetDateTime

/**
 * Creates a [OffsetDateTime] column
 */
fun TableDefinition.offsetDatetime(name: String): ColumnDefinition<OffsetDateTime> {
    return createColumn(name, OffsetDateTimeColumnType)
}

/**
 * Creates a [Int[]] column
 */
fun TableDefinition.intArray(name: String): ColumnDefinition<Array<Int>> {
    return createColumn(name, IntArrayColumnType)
}

/**
 * Creates a [String[]] column
 */
fun TableDefinition.textArray(name: String): ColumnDefinition<Array<String>> {
    return createColumn(name, TextArrayColumnType)
}

/**
 * Creates a [Json] column
 */
fun TableDefinition.jsonb(name: String): ColumnDefinition<Json> {
    return createColumn(name, JsonbColumnType)
}

