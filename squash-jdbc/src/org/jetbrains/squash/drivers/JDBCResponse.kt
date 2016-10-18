package org.jetbrains.squash.drivers

import org.jetbrains.squash.results.*
import java.sql.*

class JDBCResponse(val conversion: JDBCDataConversion, val resultSet: ResultSet) : Response {
    private val metadata = resultSet.metaData
    val columns = (1..metadata.columnCount).map {
        index ->
        val name = metadata.getColumnName(index) // name of the column
        val table = metadata.getTableName(index) ?: "" // table of the column
        val label = metadata.getColumnLabel(index) // label in query, aka "AS" alias
        val nullable = metadata.isNullable(index) == ResultSetMetaData.columnNullable

        @Suppress("UNUSED_VARIABLE")
        val klass = metadata.getColumnClassName(index) // java class name to bind type to

        @Suppress("UNUSED_VARIABLE")
        val dbtype = metadata.getColumnTypeName(index) // database type
        JDBCResponseColumn(index, label, table, name, nullable)
    }
    private val columnMap = columns.groupBy { it.label.toLowerCase() }

    private var rowsAcquired = false
    val rows: JDBCResponseRowSequence get() {
        require(!rowsAcquired) { "ResponseRow sequence has already been acquired" }
        rowsAcquired = true
        return JDBCResponseRowSequence()
    }

    inner class JDBCResponseRowSequence() : Sequence<JDBCResultRow> {
        val empty = !resultSet.next()
        override operator fun iterator(): Iterator<JDBCResultRow> = object : Iterator<JDBCResultRow> {
            var hasNext = !empty
            override fun hasNext(): Boolean = hasNext
            override fun next(): JDBCResultRow {
                val data = columns.indices.map { resultSet.getObject(it + 1) }
                val row = JDBCResultRow(data, columnMap, conversion)
                hasNext = resultSet.next()
                return row
            }
        }
    }

    override fun iterator(): Iterator<ResultRow> = rows.iterator()

    override fun toString(): String = "JDBCResponse$columns"
}

class JDBCResponseColumn(val columnIndex: Int,
                         val label: String,
                         val table: String,
                         val name: String,
                         val nullable: Boolean
) {
    override fun toString(): String = "$label@$table"
}

