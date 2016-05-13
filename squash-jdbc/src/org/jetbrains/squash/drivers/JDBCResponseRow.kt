package org.jetbrains.squash.drivers

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.results.*
import java.sql.*

class JDBCResponseRow(val response: JDBCResponse, val resultSet: ResultSet) : ResponseRow {
    val rowData = mutableMapOf<JDBCResponseColumn, Any?>()

    init {
        response.columns.forEach { column ->
            rowData[column] = getValue(column)
        }
    }

    private fun getValue(column: JDBCResponseColumn): Any? {
        val value : Any? = when (column.type) {
            is IntColumnType -> resultSet.getInt(column.columnIndex)
            is StringColumnType -> resultSet.getString(column.columnIndex)
            is LongColumnType -> resultSet.getLong(column.columnIndex)
            is BooleanColumnType -> resultSet.getBoolean(column.columnIndex)
            else -> error("Cannot get value for ${column.type} for column `$column`")
        }
        if (column.nullable && resultSet.wasNull())
            return null
        return value
    }

    @Suppress("UNCHECKED_CAST")
    override fun <V> get(column: Column<V>): V {
        val label = if (column is AliasColumn) column.label.id else column.name.id
        val columnData = rowData.entries.filter { it.key.label.equals(label, ignoreCase = true) }
        return columnValue(column.name.id, columnData)
    }

    @Suppress("UNCHECKED_CAST")
    fun <V> get(column: JDBCResponseColumn): V {
        val columnData = rowData.entries.filter { it.key.label.equals(column.name, ignoreCase = true) }
        return columnValue(column.name, columnData)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <V> get(name: String): V {
        val columnData = rowData.entries.filter { it.key.label.equals(name, ignoreCase = true) }
        return columnValue(name, columnData)
    }

    private fun <V> columnValue(name: String, columnData: List<Map.Entry<JDBCResponseColumn, Any?>>): V {
        when (columnData.size) {
            0 -> error("Cannot find data with label '$name' in response.")
            1 -> {
                val value = columnData[0].value

                @Suppress("UNCHECKED_CAST")
                return value as V
            }
            else -> error("Ambiguous label '$name', ${columnData.size} items in response.")
        }
    }
}