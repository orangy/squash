package org.jetbrains.squash.statements

import org.jetbrains.squash.definition.*
import java.util.*
import kotlin.internal.*

open class ColumnValuesBuilder() {
    val values: MutableMap<Column<*>, Any?> = LinkedHashMap()

    operator fun <V, S : V> set(column: Column<@Exact V>, value: S?) {
        if (values.containsKey(column)) {
            error("$column is already initialized")
        }
        if (column !is NullableColumn<*> && value == null) {
            error("Trying to set null to not nullable column $column")
        }
        values[column] = value
    }

    operator fun <V> get(column: Column<V>): Any? = values[column]
}
