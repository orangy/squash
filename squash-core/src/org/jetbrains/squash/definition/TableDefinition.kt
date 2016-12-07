package org.jetbrains.squash.definition

import org.jetbrains.squash.query.*

/**
 * Represents a definition of a table in a database
 */
open class TableDefinition(name: String? = null) : Table {
    override val compoundName = Identifier(name ?: javaClass.simpleName.removeSuffix("Table"))

    override fun toString(): String = "$compoundName"

    override val constraints: TableConstraints = object : TableConstraints {
        override var primaryKey: PrimaryKeyConstraint? = null
        override val elements = mutableListOf<TableConstraint>()
        override fun add(constraint: TableConstraint) {
            elements.add(constraint)
        }
    }

    private val _tableColumns = mutableListOf<ColumnDefinition<*>>()
    override val compoundColumns: List<ColumnDefinition<*>> get() = _tableColumns

    fun <T, TColumn : ColumnDefinition<T>> addColumn(column: TColumn): TColumn {
        require(column.compound == this) { "Can't add column '$column' from different table" }
        require(compoundColumns.none { it.name.id.equals(column.name.id, ignoreCase = true) }) { "Column with the same identifier '$column' already exists" }
        _tableColumns.add(column)
        return column
    }

    fun <T, C : ColumnType> createColumn(name: String, type: C): ColumnDefinition<T> {
        return addColumn(ColumnDefinition<T>(this, Identifier(name), type))
    }
}

