package org.jetbrains.squash

import java.util.*

open class Table(name: String = "") {
    open val tableName = if (name.length > 0) name else javaClass.simpleName.removeSuffix("Table")

    private val _tableColumns = ArrayList<Column<*>>()
    val tableColumns: List<Column<*>> get() = _tableColumns

    fun <T, C : ColumnType> createColumn(name: String, type: C): Column<T> {
        val column = TableColumn<T>(this, name, type)
        _tableColumns.add(column)
        return column
    }

    fun <T> replaceColumn(original: Column<T>, replacement: Column<T>): Column<T> {
        val index = _tableColumns.indexOf(original)
        if (index < 0) error("Original column `$original` not found in this table `$this`")
        _tableColumns[index] = replacement
        return replacement
    }

    override fun toString(): String = javaClass.simpleName
}

data class TableColumn<T>(override val table: Table, override val name: String, override val type: ColumnType) : Column<T>

