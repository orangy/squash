package org.jetbrains.squash.statements

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.query.*
import java.util.*
import kotlin.internal.*

fun <T : Table> update(table: T): UpdateQueryStatement<T> = UpdateQueryStatement(table)

open class UpdateQueryStatement<T : Table>(val table: T) : QueryBuilder(), Statement<Unit> {
    val values: MutableMap<Column<*>, Expression<*>> = LinkedHashMap()

    operator fun <V, S : V> set(column: Column<@Exact V>, value: Expression<S>) {
        if (values.containsKey(column)) error("$column is already initialized")
        values[column] = value
    }

    operator fun <V, S : V> set(column: Column<@Exact V>, value: S?): UpdateQueryStatement<T> {
        if (values.containsKey(column)) {
            error("$column is already initialized")
        }
        if (column.properties.all { it !is NullableProperty } && value == null) {
            error("Trying to set null to not nullable column $column")
        }
        values[column] = literal(value)
        return this
    }
}

fun <T : Table> UpdateQueryStatement<T>.set(body: T.(UpdateQueryStatement<*>) -> Unit): UpdateQueryStatement<T> {
    table.body(this)
    return this
}

