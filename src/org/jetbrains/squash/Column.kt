package org.jetbrains.squash

interface Column<T> {
    val table: Table
    val name: String
    val type: ColumnType
}

class PrimaryKeyColumn<T>(val column: Column<T>) : Column<T> by column
class NullableColumn<T>(val column: Column<T>) : Column<T> by column
class AutoIncrementColumn<T>(val column: Column<T>) : Column<T> by column