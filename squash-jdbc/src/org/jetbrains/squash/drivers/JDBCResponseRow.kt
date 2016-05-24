package org.jetbrains.squash.drivers

import org.jetbrains.squash.results.*
import java.sql.*
import kotlin.reflect.*

class JDBCResponseRow(resultSet: ResultSet, columns: List<JDBCResponseColumn>, val conversion: JDBCDataConversion) : ResponseRow {
    private val data = columns.associateBy({ it }, { resultSet.getObject(it.columnIndex) })

    override fun <V> columnValue(type: KClass<*>, name: String): V {
        val columnData = data.entries.filter { it.key.label.equals(name, ignoreCase = true) }
        return columnValue(name, type, columnData)
    }

    override fun <V> columnValue(type: KClass<*>, index: Int): V {
        val columnData = data.entries.filter { it.key.columnIndex == index + 1 }
        return columnValue("?" + index.toString(), type, columnData)
    }

    private fun <V> columnValue(name: String, type: KClass<*>, columnData: List<Map.Entry<JDBCResponseColumn, Any?>>): V {
        when (columnData.size) {
            0 -> error("Cannot find data with label '$name' in response.")
            1 -> {
                val value = columnData[0].value

                @Suppress("UNCHECKED_CAST")
                return conversion.convertValueFromDatabase(value, type) as V
            }
            else -> error("Ambiguous label '$name', ${columnData.size} items in response.")
        }
    }
}

operator inline fun <reified V> JDBCResponseRow.get(column: JDBCResponseColumn): V = columnValue(V::class, column.columnIndex - 1)
