package org.jetbrains.squash.drivers

import org.jetbrains.squash.results.*
import java.sql.*

class JDBCResponse(val transaction: JDBCTransaction, val resultSet: ResultSet) : Response {
    val metadata = resultSet.metaData
    val columns = mutableListOf<JDBCResponseColumn>()
    val rows = JDBCResponseRowSequence(this)

    init {
        (1..metadata.columnCount).forEach { index -> createColumn(index) }
    }

    override fun iterator(): Iterator<ResponseRow> = rows.iterator()

    private fun createColumn(index: Int) {
        val name = metadata.getColumnName(index) // name of the column
        val table = metadata.getTableName(index) // table of the column
        val label = metadata.getColumnLabel(index) // label in query, aka "AS" alias
        val nullable = metadata.isNullable(index) == ResultSetMetaData.columnNullable

        @Suppress("UNUSED_VARIABLE")
        val klass = metadata.getColumnClassName(index) // java class name to bind type to

        @Suppress("UNUSED_VARIABLE")
        val dbtype = metadata.getColumnTypeName(index) // database typer
        columns.add(JDBCResponseColumn(index, label, table, name, nullable))
    }
}

class JDBCResponseColumn(val columnIndex: Int,
                         val label: String,
                         val table: String,
                         val name: String,
                         val nullable: Boolean
) {

    override fun toString(): String = "JDBCResponseColumn('$label')"
}

class JDBCResponseRowSequence(val response: JDBCResponse) : Sequence<JDBCResponseRow> {
    val empty = !response.resultSet.next()
    override operator fun iterator(): Iterator<JDBCResponseRow> = object : Iterator<JDBCResponseRow> {
        var hasNext = !empty
        override fun hasNext(): Boolean = hasNext
        override fun next(): JDBCResponseRow = JDBCResponseRow(response, response.resultSet).apply {
            hasNext = response.resultSet.next()
        }
    }
}