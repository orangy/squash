package org.jetbrains.squash.drivers

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.results.*
import org.jetbrains.squash.statements.*
import java.sql.*

class JDBCResponse(val resultSet: ResultSet, configure: ColumnOwner.() -> Unit = {}) : Response, ColumnOwnerImpl() {
    val metadata = resultSet.metaData
    val rows = JDBCResponseRowSequence(this)

    init {
        configure()
        if (tableColumns.size == 0)
            (1..metadata.columnCount).map { index -> createColumn(index) }
    }

    private fun createColumn(index: Int): Column<Any?> {
        val baseColumn = createColumn<Any, ColumnType>(metadata.getColumnName(index), columnType(index))
        if (metadata.isNullable(index) == ResultSetMetaData.columnNullable)
            return replaceColumn(baseColumn, NullableColumn(baseColumn))

        return baseColumn
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


class JDBCResponseRow(val response: JDBCResponse, val resultSet: ResultSet) : ResponseRow {
    val rowData = ColumnValuesBuilder()

    init {
        response.tableColumns.forEach { column ->
            val index = resultSet.findColumn(column.name.id)
            rowData[column] = getValue(index, column)
        }
    }

    private fun getValue(columnIndex: Int, column: Column<*>): Any? {
        when (column) {
            is NullableColumn<*> -> {
                val value = getValue(columnIndex, column.column)
                if (resultSet.wasNull())
                    return null
                return value
            }
            is DataColumn<*> -> {
                return when (column.type) {
                    is IntColumnType -> resultSet.getInt(columnIndex)
                    is StringColumnType -> resultSet.getString(columnIndex)
                    is LongColumnType -> resultSet.getLong(columnIndex)
                    is BooleanColumnType -> resultSet.getBoolean(columnIndex)
                    else -> error("Cannot get value for ${column.type} for column `$column`")
                }
            }
            else -> error("Unknown column type ${column.javaClass} for column '$column'")
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <V> get(column: Column<V>): V = rowData[column] as V

    @Suppress("UNCHECKED_CAST")
    override fun <V> get(name: String): V = rowData.values.entries.single { it.key.name.id == name }.value as V
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