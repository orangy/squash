package org.jetbrains.squash.expressions

import org.jetbrains.squash.*

val query: Query get() = QueryBuilder()

fun Table.where(selector: () -> Expression<Boolean>) = query.from(this).where(selector)

interface Query {
    val structure : List<Table>
    val selection : List<Expression<*>>
    val filter : List<Expression<*>>

    /**
     * Adds [table] to the list of tables to fetch data from.
     */
    fun from(table: Table): Query

    /**
     * Adds [predicate] to the Query, filtering result set by only rows matching it
     */
    fun where(predicate: () -> Expression<Boolean>): Query

    /**
     * Adds [expression] to the list of fields to be retrieved from the result set
     */
    fun <T> select(expression: () -> Expression<T>): Query
}

