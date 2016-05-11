package org.jetbrains.squash.statements

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.expressions.*

open class UpdateStatementSeed<T : Table>(val table: T)

open class InsertValuesStatement<T : Table, R>(val table: T, val values: Map<Column<*>, Any?>) : Statement<R> {
    override fun forEachArgument(body: (Column<*>, Any?) -> Unit) {
        values.forEach { body(it.key, it.value) }
    }
    // TODO: there can be more than one generated key
    var generatedKeyColumn: Column<R>? = null
}

fun <T : Table> insertInto(table: T): UpdateStatementSeed<T> = UpdateStatementSeed(table)

fun <T : Table> UpdateStatementSeed<T>.values(body: T.(ColumnValuesBuilder) -> Unit): InsertValuesStatement<T, Unit> {
    val values = ColumnValuesBuilder()
    table.body(values)
    return InsertValuesStatement(table, values.values)
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


open class InsertQueryStatement<T : Table>(val table: T) : QueryBuilder(), Statement<Unit> {
    override fun forEachArgument(body: (Column<*>, Any?) -> Unit) {}
}

fun <T : Table> UpdateStatementSeed<T>.from(from: Table): InsertQueryStatement<T> {
    return InsertQueryStatement(table).from(from)
}
