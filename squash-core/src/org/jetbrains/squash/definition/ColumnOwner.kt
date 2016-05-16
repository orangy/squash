package org.jetbrains.squash.definition

import org.jetbrains.squash.*
import java.util.*

interface ColumnOwner : ConstraintOwner {
    val tableColumns: List<Column<*>>

    fun <T, C : ColumnType> createColumn(name: String, type: C): Column<T>
    fun <T1, T2> replaceColumn(original: Column<T1>, replacement: Column<T2>): Column<T2>
    fun <T> addColumn(column: Column<T>): Column<T>
}
