package org.jetbrains.squash.statements

import org.jetbrains.squash.*
import org.jetbrains.squash.definition.*

open class InsertStatement<T : Table, R>(val transaction: Transaction, val table: T) : ColumnValuesBuilder(), Statement<R> {

    override fun forEachParameter(body: (Column<*>, Any?) -> Unit) {
        values.forEach { body(it.key, it.value) }
    }

    // TODO: there can be more than one generated key
    var generatedKeyColumn: Column<R>? = null
}

fun <T : Table> Transaction.insertInto(table: T): InsertStatement<T, Unit> = InsertStatement(this, table)

fun <T : Table> Transaction.insertInto(table: T, body: T.(InsertStatement<T, Unit>) -> Unit) {
    val statement = InsertStatement<T, Unit>(this, table)
    body(table, statement)
    executeStatement(statement)
}

fun <T : Table, R> InsertStatement<T, Unit>.fetch(column: Column<R>): InsertStatement<T, R> {
    @Suppress("CAST_NEVER_SUCCEEDS")
    return (this as InsertStatement<T, R>).apply {
        generatedKeyColumn = column
    }
}

fun <T : Table, R> InsertStatement<T, Unit>.fetch(column: Column<R>, body: T.(InsertStatement<T, R>) -> Unit): R {
    val statement = fetch(column)
    body(table, statement)
    return transaction.executeStatement(statement)
}

@JvmName("fetch_error")
@Deprecated("'fetch' cannot be used on an already bound InsertStatement.", ReplaceWith(""), DeprecationLevel.ERROR)
@Suppress("UNUSED_PARAMETER", "unused")
fun <T : Table, R> InsertStatement<T, *>.fetch(column: Column<R>, body: T.(InsertStatement<T, R>) -> Unit): R = error("'fetch' cannot be used on an already bound InsertStatement.")

