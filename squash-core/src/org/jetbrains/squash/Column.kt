package org.jetbrains.squash

import org.jetbrains.squash.expressions.*

interface Column<T> : NamedExpression<Name, T> {
    val owner: ColumnOwner
    val type: ColumnType
}

data class DataColumn<T>(override val owner: ColumnOwner, override val name: Name, override val type: ColumnType) : Column<T> {
    override fun toString(): String = "$owner.$name: $type"
}

class PrimaryKeyColumn<T>(val column: Column<T>) : Column<T> by column {
    override fun toString(): String = "[PK] $column"
}

class NullableColumn<T>(val column: Column<T>) : Column<T?> {
    override val owner: ColumnOwner get() = column.owner
    override val type: ColumnType get() = NullableColumnType(column.type)
    override val name: Name get() = column.name

    override fun toString(): String = "$column?"

}

class DefaultValueColumn<T>(val column: Column<T>, val value: T) : Column<T> by column {
    override fun toString(): String = "$column = $value"
}

class AutoIncrementColumn<T>(val column: Column<T>) : Column<T> by column {
    override fun toString(): String = "$column++"
}