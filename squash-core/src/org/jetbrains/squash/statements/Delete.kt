package org.jetbrains.squash.statements

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.query.*
import java.util.*
import kotlin.internal.*

fun <T : Table> deleteFrom(table: T): DeleteQueryStatement<T> = DeleteQueryStatement(table)

open class DeleteQueryStatement<T : Table>(val table: T) : QueryBuilder(), Statement<Unit> {
    val values: MutableMap<Column<*>, Expression<*>> = LinkedHashMap()

    operator fun <V, S : V> set(column: Column<@Exact V>, value: Expression<S>) {
        if (values.containsKey(column)) error("$column is already initialized")
        values[column] = value
    }

    operator fun <V, S : V> set(column: Column<@Exact V>, value: S?) {
        if (values.containsKey(column)) {
            error("$column is already initialized")
        }
        if (column !is NullableColumn<*, *> && value == null) {
            error("Trying to set null to not nullable column $column")
        }
        values[column] = literal(value)
    }
}

