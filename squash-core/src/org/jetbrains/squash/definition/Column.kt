package org.jetbrains.squash.definition

/**
 * Represents a column in a database [Table]
 *
 * Column is also a [NamedExpression] which allows it to be used in expressions.
 *
 * @param V type of the value in this column
 */
interface Column<out V> : NamedExpression<Name, V> {
    /**
     * [Table] to which this column belongs
     */
    val table: Table

    /**
     * Database type of the column
     */
    val type: ColumnType
}

data class DataColumn<out V>(override val table: Table, override val name: Name, override val type: ColumnType) : Column<V> {
    override fun toString(): String = "$name: $type"
}

class NullableColumn<out V, out TColumn : Column<V>>(val column: TColumn) : Column<V?> {
    override val table: Table get() = column.table
    override val type: ColumnType = NullableColumnType(column.type)
    override val name: Name get() = column.name

    override fun toString(): String = "$column?"
}

class ReferenceColumn<out V>(override val table: Table, override val name: Name, val reference: Column<V>) : Column<V> {
    override val type: ColumnType = ReferenceColumnType(reference.type)
    override fun toString(): String = "&$reference"
}

class DefaultValueColumn<out V>(val column: Column<V>, val value: V) : Column<V> by column {
    override fun toString(): String = "$column = $value"
}

class AutoIncrementColumn<out V>(val column: Column<V>) : Column<V> by column {
    override fun toString(): String = "$column++"
}