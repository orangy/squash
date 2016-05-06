package org.jetbrains.squash

import org.jetbrains.squash.expressions.*
import java.util.*

open class ColumnOwner() : FieldCollection {
    private val _tableColumns = ArrayList<Column<*>>()

    val tableColumns: List<Column<*>> get() = _tableColumns
    override val fields: List<Expression<*>> get() = _tableColumns

    fun <T, C : ColumnType> createColumn(name: String, type: C): Column<T> {
        val column = DataColumn<T>(this, columnName(name), type)
        _tableColumns.add(column)
        return column
    }

    fun <T> replaceColumn(original: Column<T>, replacement: Column<T>): Column<T> {
        val index = _tableColumns.indexOf(original)
        if (index < 0) error("Original column `$original` not found in this table `$this`")
        _tableColumns[index] = replacement
        return replacement
    }

    open fun columnName(name: String): Name = Identifier(name)
}

open class Table(name: String? = null) : ColumnOwner() {
    open val tableName = Identifier(name ?: javaClass.simpleName.removeSuffix("Table"))
    override fun toString(): String = "[$tableName]"

    override fun columnName(name: String): Name = QualifiedIdentifier(tableName, Identifier(name))
}

