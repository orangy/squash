package org.jetbrains.squash.statements

import org.jetbrains.squash.*

open class InsertStatement<T : Table, R>(val transaction: Transaction, val table: T) : ColumnValuesBuilder(), Statement<R> {

    override fun forEachParameter(body: (Column<*>, Any?) -> Unit) {
        values.forEach { body(it.key, it.value) }
    }

    var fetchColumn: Column<R>? = null
}

fun <T : Table, R> InsertStatement<T, Unit>.fetch(column: Column<R>): InsertStatement<T, R> {
    @Suppress("CAST_NEVER_SUCCEEDS")
    return (this as InsertStatement<T, R>).apply {
        fetchColumn = column
    }
}

fun <T : Table, R> InsertStatement<T, Unit>.fetch(column: Column<R>, body: T.(InsertStatement<T, R>) -> Unit): R {
    val statement = fetch(column)
    body(table, statement)
    return transaction.execute(statement)
}

fun <T : Table> Transaction.insertInto(table: T): InsertStatement<T, Unit> = InsertStatement(this, table)

fun <T : Table> Transaction.insertInto(table: T, body: T.(InsertStatement<T, Unit>) -> Unit) {
    val statement = InsertStatement<T, Unit>(this, table)
    body(table, statement)
    execute(statement)
}
