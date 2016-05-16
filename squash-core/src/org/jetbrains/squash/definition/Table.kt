package org.jetbrains.squash.definition

import org.jetbrains.squash.*

open class Table(name: String? = null) : ColumnOwner, ConstraintOwner {
    open val tableName = Identifier(name ?: javaClass.simpleName.removeSuffix("Table"))

    override fun toString(): String = "$tableName"


    private val _constraints = mutableListOf<Constraint>()
    override val constraints: List<Constraint> get() = _constraints
    fun addConstraint(constraint: Constraint) {
        _constraints.add(constraint)
    }

    private val _tableColumns = mutableListOf<Column<*>>()
    override val tableColumns: List<Column<*>> get() = _tableColumns
    override fun <T> addColumn(column: Column<T>): Column<T> {
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

    private fun columnName(name: String): Name = QualifiedIdentifier(tableName, Identifier(name))
}

