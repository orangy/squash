package org.jetbrains.squash.drivers

import org.jetbrains.squash.results.*
import java.sql.*
import kotlin.reflect.*

class JDBCResultRow(val data: List<Any?>, val columns: Map<String, List<JDBCResponseColumn>>, val conversion: JDBCDataConversion) : ResultRow {
    override fun columnValue(type: KClass<*>, columnName: String, tableName: String?): Any? {
        val dataForName = columns[columnName.toLowerCase()] ?: return null
        val columnData = if (tableName == null)
            dataForName
        else
            dataForName.filter { it.table.equals(tableName, ignoreCase = true) }
        return columnValue("$columnName@${tableName ?: ""}", type, columnData)
    }

    override fun columnValue(type: KClass<*>, index: Int): Any? {
        val value = data[index]
        return conversion.convertValueFromDatabase(value, type)
    }

    private fun columnValue(label: String, type: KClass<*>, columnData: List<JDBCResponseColumn>): Any? {
        when (columnData.size) {
            0 -> return null
            1 -> {
                val value = data[columnData[0].columnIndex - 1]

                @Suppress("UNCHECKED_CAST")
                return conversion.convertValueFromDatabase(value, type)
            }
            else -> error("Ambiguous label '$label', ${columnData.size} items in response.")
        }
    }
}

