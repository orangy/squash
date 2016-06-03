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

    val rows = JDBCResponseRowSequence()

    inner class JDBCResponseRowSequence() : Sequence<JDBCResponseRow> {
        val empty = !resultSet.next()
        override operator fun iterator(): Iterator<JDBCResponseRow> = object : Iterator<JDBCResponseRow> {
            var hasNext = !empty
            override fun hasNext(): Boolean = hasNext
            override fun next(): JDBCResponseRow = JDBCResponseRow(resultSet, columns, conversion).apply {
                hasNext = resultSet.next()
            }
        }
    }

    override fun iterator(): Iterator<ResponseRow> = rows.iterator()
}

class JDBCResponseColumn(val columnIndex: Int,
                         val label: String,
                         val table: String,
                         val name: String,
                         val nullable: Boolean
) {

    override fun toString(): String = "JDBCResponseColumn('$label')"
}

