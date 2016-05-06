package org.jetbrains.squash

import org.jetbrains.squash.expressions.*
import java.util.*

open class Table(name: String = "") : FieldCollection {
    open val tableName = if (name.length > 0) name else javaClass.simpleName.removeSuffix("Table")

    private val _tableColumns = ArrayList<Column<*>>()
    val tableColumns: List<Column<*>> get() = _tableColumns

    override val fields: List<Expression<*>> get() = _tableColumns

    fun <T, C : ColumnType> createColumn(name: String, type: C): Column<T> {
        val column = TableColumn<T>(this, Identifier(name), type)
        _tableColumns.add(column)
        return column
    }

    fun <T> replaceColumn(original: Column<T>, replacement: Column<T>): Column<T> {
        val index = _tableColumns.indexOf(original)
        if (index < 0) error("Original column `$original` not found in this table `$this`")
        _tableColumns[index] = replacement
        return replacement
    }

    override fun toString(): String = "[$tableName]"
}

