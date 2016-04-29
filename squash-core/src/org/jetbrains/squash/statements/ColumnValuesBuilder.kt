package org.jetbrains.squash.statements

import org.jetbrains.squash.*
import java.util.*

abstract class ColumnValuesBuilder() {
    val values: MutableMap<Column<*>, Any?> = LinkedHashMap()

    operator fun <T, S : T> set(column: Column<T>, value: S?) {
        if (values.containsKey(column)) {
            error("$column is already initialized")
        }
        if (column !is NullableColumn && value == null) {
            error("Trying to set null to not nullable column $column")
        }
        values[column] = value
    }
}
