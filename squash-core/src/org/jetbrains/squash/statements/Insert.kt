package org.jetbrains.squash.statements

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.query.*
import java.util.*
import kotlin.internal.*

open class InsertStatementSeed<T : Table>(val table: T)

open class InsertValuesStatement<T : Table, R>(val table: T) : Statement<R> {
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

    // TODO: there can be more than one generated key
    var generatedKeyColumn: Column<R>? = null
}

fun <T : Table> insertInto(table: T): InsertStatementSeed<T> = InsertStatementSeed(table)

fun <T : Table> InsertStatementSeed<T>.values(body: T.(InsertValuesStatement<T, Unit>) -> Unit): InsertValuesStatement<T, Unit> {
    val values = InsertValuesStatement<T, Unit>(table)
    table.body(values)
    return values
}

fun <T : Table, R> InsertValuesStatement<T, Unit>.fetch(column: Column<R>): InsertValuesStatement<T, R> {
    @Suppress("CAST_NEVER_SUCCEEDS")
    return (this as InsertValuesStatement<T, R>).apply {
        generatedKeyColumn = column
    }
}

@JvmName("fetch_error")
@Deprecated("'fetch' cannot be used on an already bound InsertStatement.", ReplaceWith(""), DeprecationLevel.ERROR)
@Suppress("UNUSED_PARAMETER", "unused")
fun <T : Table, R> InsertValuesStatement<T, *>.fetch(column: Column<R>, body: T.(InsertValuesStatement<T, R>) -> Unit): R = error("'fetch' cannot be used on an already bound InsertStatement.")

open class InsertQueryStatement<T : Table>(val table: T) : Statement<Sequence<Nothing>>, QueryBuilder()

fun <T : Table> InsertStatementSeed<T>.query(): InsertQueryStatement<T> = InsertQueryStatement(table)
