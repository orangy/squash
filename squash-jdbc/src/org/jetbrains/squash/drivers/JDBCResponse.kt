package org.jetbrains.squash.drivers

import org.jetbrains.squash.results.*
import java.sql.*

class JDBCResponse(val conversion: JDBCDataConversion, val resultSet: ResultSet) : Response {
    private val metadata = resultSet.metaData
    val columns = (1..metadata.columnCount).map { dbColumnIndex ->
        val name = metadata.getColumnName(dbColumnIndex) // name of the column
        val table = metadata.getTableName(dbColumnIndex) ?: "" // table of the column
        val label = metadata.getColumnLabel(dbColumnIndex) // label in query, aka "AS" alias
        val nullable = metadata.isNullable(dbColumnIndex) == ResultSetMetaData.columnNullable
        val databaseType = metadata.getColumnTypeName(dbColumnIndex) // database type

        @Suppress("UNUSED_VARIABLE")
        val klass = metadata.getColumnClassName(dbColumnIndex) // java class name to bind type to

        JDBCResponseColumn(dbColumnIndex, label, databaseType, table, name, nullable)
    }

    private val columnMap = columns.groupBy { it.label.toLowerCase() }

    private var rowsAcquired = false

    override operator fun iterator(): JDBCResultRowIterator {
        require(!rowsAcquired) { "ResponseRow sequence has already been acquired" }
        rowsAcquired = true
        return JDBCResultRowIterator()
    }

    inner class JDBCResultRowIterator : Iterator<JDBCResultRow> {
        var hasNext = resultSet.next()
        override fun hasNext(): Boolean = hasNext
        override fun next(): JDBCResultRow {
            val data = Array(columns.size) { columnIndex ->
                conversion.fetch(resultSet, columnIndex + 1, columns[columnIndex])
            }
            val row = JDBCResultRow(data, columnMap, conversion)
            hasNext = resultSet.next()
            return row
        }
    }

    override fun toString(): String = "JDBCResponse$columns"
}

class JDBCResponseColumn(
    val columnIndex: Int,
    val label: String,
    val databaseType: String,
    val table: String,
    val name: String,
    val nullable: Boolean
) {
    override fun toString(): String = "$label@$table"
}

