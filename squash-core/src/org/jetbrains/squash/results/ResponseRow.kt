package org.jetbrains.squash.results

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.expressions.*
import kotlin.reflect.*

/**
 * Represents a response row.
 */
interface ResponseRow {
    /**
     * Gets value of a column with the given [type], [columnName] and optional [tableName]
     */
    fun <V> columnValue(type: KClass<*>, columnName: String, tableName: String? = null): V

    /**
     * Gets value of a column with the given [type] and [index]
     */
    fun <V> columnValue(type: KClass<*>, index: Int): V
}

operator inline fun <reified V> ResponseRow.get(name: String): V = columnValue(V::class, name)
operator inline fun <reified V> ResponseRow.get(index: Int): V = columnValue(V::class, index)
operator inline fun <reified V> ResponseRow.get(column: Column<V>): V = columnValue(V::class, column)

fun <V> ResponseRow.columnValue(type: KClass<*>, column: Column<V>): V {
    val label = if (column is AliasColumn) column.label.id else column.name.id
    return columnValue(type, label, column.table.tableName.id)
}
