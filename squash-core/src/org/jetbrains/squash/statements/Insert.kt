package org.jetbrains.squash.statements

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.query.*
import kotlin.internal.*

open class InsertStatementSeed<T : Table>(val table: T)

open class InsertValuesStatement<T : Table, R>(val table: T) : Statement<R> {
    val values: MutableMap<Column<*>, Any?> = LinkedHashMap()

    operator fun <V, S : V> set(column: Column<@Exact V>, value: S?) {
		// Error if the column is being set more than once
        if (values.containsKey(column)) {
            error("$column is already initialized")
        }

		val finalValue = value ?: if (column.hasProperty<DefaultValueProperty<S>>())
			column.propertyOrNull<DefaultValueProperty<S>>()!!.value
		else
			null

		// Error if the column is not nullable and it is getting set to null
        if (column.properties.none { it is NullableProperty } && finalValue == null) {
            error("Trying to set null to not nullable column $column")
        }

        values[column] = finalValue
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
    require(generatedKeyColumn == null) { "Only one fetch column is supported."}
    @Suppress("UNCHECKED_CAST")
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
