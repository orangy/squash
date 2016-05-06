package org.jetbrains.squash.expressions

import org.jetbrains.squash.*

val query: Query get() = QueryBuilder()

fun Table.where(selector: () -> Expression<Boolean>) = query.from(this).where(selector)

interface Query {
    val structure : List<QueryStructure>
    val selection : List<Expression<*>>
    val filter : List<Expression<*>>

    /**
     * Adds [table] to the structure
     */
    fun from(table: Table): Query

    /**
     * Adds a join operation to the structure
     */
    fun innerJoin(target: Table, on: () -> Expression<Boolean>): Query

    /**
     * Adds [predicate] to the Query, filtering result set by only rows matching it
     */
    fun where(predicate: () -> Expression<Boolean>): Query

    /**
     * Adds [expression] to the list of fields to be retrieved from the result set
     */
    fun <T> select(expression: () -> Expression<T>): Query

}

