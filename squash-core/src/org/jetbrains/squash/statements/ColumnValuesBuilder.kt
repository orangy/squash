package org.jetbrains.squash.statements

import org.jetbrains.squash.definition.*
import java.util.*

abstract class ColumnValuesBuilder() {
    val values: MutableMap<Column<*>, Any?> = LinkedHashMap()

    operator fun <V, S : V> set(column: Column<V>, value: S?) {
        if (values.containsKey(column)) {
            error("$column is already initialized")
        }
        if (column !is NullableColumn<*> && value == null) {
            error("Trying to set null to not nullable column $column")
        }
        values[column] = value
    }
}
