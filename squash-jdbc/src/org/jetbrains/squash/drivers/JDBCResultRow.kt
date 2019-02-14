package org.jetbrains.squash.drivers

import org.jetbrains.squash.results.*
import kotlin.reflect.*

class JDBCResultRow(
    private val data: Array<Any?>,
    private val columns: Map<String, List<JDBCResponseColumn>>,
    private val conversion: JDBCDataConversion
) : ResultRow {
    
    override fun columnValue(type: KClass<*>, columnName: String, tableName: String?): Any? {
        val dataForName = columns[columnName] ?: columns[columnName.toLowerCase()] ?: return null
        if (tableName == null) {
            return when (dataForName.size) {
                0 -> null
                1 -> {
                    val value = data[dataForName[0].columnIndex - 1]
                    conversion.convertValueFromDatabase(value, type)
                }
                else -> error("Ambiguous labels $dataForName")
            }
        }

        var foundIndex = -1
        dataForName.forEachIndexed { index, column ->
            if (column.table.equals(tableName, ignoreCase = true)) {
                if (foundIndex != -1) {
                    error("Ambiguous labels [${dataForName[foundIndex]}, ${dataForName[index]}]")
                }
                foundIndex = index
            }
        }
        if (foundIndex == -1) 
            return null
        
        val value = data[dataForName[foundIndex].columnIndex - 1]
        return conversion.convertValueFromDatabase(value, type)
    }

    override fun columnValue(type: KClass<*>, index: Int): Any? {
        val value = data[index]
        return conversion.convertValueFromDatabase(value, type)
    }
}

