package org.jetbrains.squash.drivers

import org.jetbrains.squash.results.*
import java.sql.*
import kotlin.reflect.*

class JDBCResultRow(resultSet: ResultSet, columns: List<JDBCResponseColumn>, val conversion: JDBCDataConversion) : ResultRow {
    private val data = columns.associateBy({ it }, { resultSet.getObject(it.columnIndex) })

    override fun columnValue(type: KClass<*>, columnName: String, tableName: String?): Any? {
        val columnData = data.entries.filter {
            it.key.label.equals(columnName, ignoreCase = true)
                    && (tableName == null || it.key.table.equals(tableName, ignoreCase = true))
        }
        return columnValue("$columnName@${tableName ?: ""}", type, columnData)
    }

    override fun columnValue(type: KClass<*>, index: Int): Any? {
        val columnData = data.entries.filter { it.key.columnIndex == index + 1 }
        return columnValue("?" + index.toString(), type, columnData)
    }

    private fun <V> columnValue(label: String, type: KClass<*>, columnData: List<Map.Entry<JDBCResponseColumn, Any?>>): V {
        when (columnData.size) {
            0 -> {
                error("Cannot find data with label '$label' in response.")
            }
            1 -> {
                val value = columnData[0].value

                @Suppress("UNCHECKED_CAST")
                return conversion.convertValueFromDatabase(value, type) as V
            }
            else -> error("Ambiguous label '$label', ${columnData.size} items in response.")
        }
    }
}

