package org.jetbrains.squash.definition

import org.jetbrains.squash.*

interface Column<out V> : NamedExpression<Name, V> {
    val table: Table
    val type: ColumnType
}

data class DataColumn<out V>(override val table: Table, override val name: Name, override val type: ColumnType) : Column<V> {
    override fun toString(): String = "$name: $type"
}

class NullableColumn<out V>(val column: Column<V>) : Column<V?> {
    override val table: Table get() = column.table
    override val type: ColumnType get() = NullableColumnType(column.type)
    override val name: Name get() = column.name

    override fun toString(): String = "$column?"
}

class DefaultValueColumn<out V>(val column: Column<V>, val value: V) : Column<V> by column {
    override fun toString(): String = "$column = $value"
}

class AutoIncrementColumn<out V>(val column: Column<V>) : Column<V> by column {
    override fun toString(): String = "$column++"
}