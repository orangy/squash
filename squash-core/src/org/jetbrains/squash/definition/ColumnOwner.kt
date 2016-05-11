package org.jetbrains.squash.definition

import org.jetbrains.squash.*
import java.util.*

interface ColumnOwner {
    val tableColumns: List<Column<*>>

    fun <T, C : ColumnType> createColumn(name: String, type: C): Column<T>
    fun <T1, T2> replaceColumn(original: Column<T1>, replacement: Column<T2>): Column<T2>
    fun <T> addColumn(column: Column<T>): Column<T>
}

open class ColumnOwnerImpl() : ColumnOwner {
    private val _tableColumns = ArrayList<Column<*>>()

    override val tableColumns: List<Column<*>> get() = _tableColumns

    override fun <T> addColumn(column : Column<T>): Column<T> {
        _tableColumns.add(column)
        return column
    }
    override fun <T, C : ColumnType> createColumn(name: String, type: C): Column<T> {
        return addColumn(DataColumn<T>(this, columnName(name), type))
    }

    override fun <T1, T2> replaceColumn(original: Column<T1>, replacement: Column<T2>): Column<T2> {
        val index = _tableColumns.indexOf(original)
        if (index < 0) error("Original column `$original` not found in this table `$this`")
        _tableColumns[index] = replacement
        return replacement
    }

    open fun columnName(name: String): Name = Identifier(name)
}
