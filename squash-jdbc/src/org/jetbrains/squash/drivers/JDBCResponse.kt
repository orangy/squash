package org.jetbrains.squash.drivers

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.results.*
import java.sql.*

class JDBCResponse(val resultSet: ResultSet) : Response {
    val metadata = resultSet.metaData
    val columns = mutableListOf<JDBCResponseColumn>()
    override val rows = JDBCResponseRowSequence(this)

    init {
        (1..metadata.columnCount).forEach { index -> createColumn(index) }
    }

    private fun createColumn(index: Int) {
        val name = metadata.getColumnName(index) // name of the column
        val table = metadata.getTableName(index) // table of the column
        val label = metadata.getColumnLabel(index) // label in query, aka "AS" alias
        val nullable = metadata.isNullable(index) == ResultSetMetaData.columnNullable

        val klass = metadata.getColumnClassName(index) // java class name to bind type to
        val dbtype = metadata.getColumnTypeName(index) // database typer
        columns.add(JDBCResponseColumn(index, label, table, name, nullable, columnType(index)))
    }

    private fun columnType(index: Int): ColumnType {
        val columnType = when (metadata.getColumnType(index)) {
            Types.CHAR -> CharColumnType
            Types.INTEGER -> IntColumnType
            Types.BIGINT -> LongColumnType
            Types.DATE -> DateColumnType
            Types.TIME -> DateTimeColumnType
            Types.BLOB -> BlobColumnType
            Types.BOOLEAN -> BooleanColumnType
            Types.DECIMAL -> DecimalColumnType(metadata.getScale(index), metadata.getPrecision(index))
            Types.VARCHAR -> StringColumnType(metadata.getPrecision(index))
            Types.BINARY -> BinaryColumnType(metadata.getPrecision(index))
            Types.SMALLINT -> IntColumnType
            else -> throw UnsupportedOperationException("Column type '${metadata.getColumnTypeName(index)}' is not supported")
        }
        return columnType
    }
}

class JDBCResponseColumn(val columnIndex: Int,
                         val label: String,
                         val table: String,
                         val name: String,
                         val nullable: Boolean,
                         val type: ColumnType) {

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