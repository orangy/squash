package org.jetbrains.squash

interface Column<T> {
    val table: Table
    val name: String
    val type: ColumnType
}

data class TableColumn<T>(override val table: Table, override val name: String, override val type: ColumnType) : Column<T> {
    override fun toString(): String = "$table.$name: $type"
}

class PrimaryKeyColumn<T>(val column: Column<T>) : Column<T> by column {
    override fun toString(): String = "[PK] $column"
}

class NullableColumn<T>(val column: Column<T>) : Column<T> by column {
    override fun toString(): String = "$column?"

}

class DefaultValueColumn<T>(val column: Column<T>, val value: T) : Column<T> by column {
    override fun toString(): String = "$column = $value"
}

class AutoIncrementColumn<T>(val column: Column<T>) : Column<T> by column {
    override fun toString(): String = "$column++"
}