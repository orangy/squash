package org.jetbrains.squash.results

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.expressions.*
import kotlin.reflect.*

/**
 * Represents a response row.
 */
interface ResultRow {
    /**
     * Gets value of a column with the given [type], [columnName] and optional [tableName]
     */
    fun columnValue(type: KClass<*>, columnName: String, tableName: String? = null): Any?

    /**
     * Gets value of a column with the given [type] and [index]
     */
    fun columnValue(type: KClass<*>, index: Int): Any?
}

operator inline fun <reified V> ResultRow.get(name: String): V = columnValue(V::class, name) as V
operator inline fun <reified V> ResultRow.get(index: Int): V = columnValue(V::class, index) as V
operator inline fun <reified V> ResultRow.get(column: Column<V>): V = columnValue(V::class, column)

fun <V> ResultRow.columnValue(type: KClass<*>, column: Column<V>): V {
    val label = if (column is AliasColumn) column.label.id else column.name.id
    return columnValue(type, label, column.table.tableName.id) as V
}

fun <V> ResultRow.columnValue(column: Column<V>): V {
    val label = if (column is AliasColumn) column.label.id else column.name.id
    return columnValue(column.type.runtimeType, label, column.table.tableName.id) as V
}
