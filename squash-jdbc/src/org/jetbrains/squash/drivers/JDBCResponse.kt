package org.jetbrains.squash.drivers

import org.jetbrains.squash.definition.*
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
        columns.add(JDBCResponseColumn(index, label, table, name, nullable, columnType(index)))
    }

    private fun columnType(index: Int): ColumnType {
        val className = metadata.getColumnClassName(index)
        val columnTypeName = metadata.getColumnTypeName(index)
        val columnType = metadata.getColumnType(index)
        return when (columnType) {
            Types.BIT -> BooleanColumnType
            Types.CHAR -> CharColumnType
            Types.INTEGER -> IntColumnType
            Types.SMALLINT -> IntColumnType
            Types.BIGINT -> LongColumnType
            Types.DATE -> DateColumnType
            Types.BLOB -> BlobColumnType
            Types.CLOB -> StringColumnType()
            Types.BOOLEAN -> BooleanColumnType
            Types.DECIMAL -> DecimalColumnType(metadata.getScale(index), metadata.getPrecision(index))
            Types.NUMERIC -> DecimalColumnType(metadata.getScale(index), metadata.getPrecision(index))
            Types.VARCHAR -> StringColumnType(metadata.getPrecision(index))
            Types.BINARY -> BinaryColumnType(metadata.getPrecision(index))
            Types.VARBINARY -> BinaryColumnType(metadata.getPrecision(index))
            Types.TIMESTAMP -> DateTimeColumnType
            else -> {
                when (className) {
                    "java.lang.Character" -> CharColumnType
                    "java.lang.Integer" -> IntColumnType
                    "java.lang.Long" -> LongColumnType
                    "java.lang.Short" -> IntColumnType
                    "java.util.UUID" -> UUIDColumnType
                    "java.lang.String" -> StringColumnType(metadata.getPrecision(index))
                    else -> error("Column type '$columnTypeName:$columnType:$className' is not supported")
                }
            }
        }
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