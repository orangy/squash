package org.jetbrains.squash.query

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.statements.*

open class QueryBuilder : Query {
    override val schema = mutableListOf<QuerySchema>()
    override val selection = mutableListOf<Expression<*>>()
    override val filter = mutableListOf<Expression<Boolean>>()
}

fun <Q : QueryBuilder> Q.copy(): QueryStatement = query().apply {
    schema.addAll(this@copy.schema)
    selection.addAll(this@copy.selection)
    filter.addAll(this@copy.filter)
}

/**
 * Adds a join operation to the structure
 */
fun <Q : QueryBuilder> Q.innerJoin(target: Table, on: Expression<Boolean>): Q = apply {
    schema.add(QuerySchema.InnerJoin(target, on))
}

fun <Q : QueryBuilder> Q.innerJoin(target: Table, on: () -> Expression<Boolean>): Q = innerJoin(target, on())

/**
 * Adds [expression] to the list of fields to be retrieved from the result set
 */
fun <T, Q : QueryBuilder> Q.select(vararg expression: Expression<T>): Q = apply {
    selection.addAll(expression)
}

fun <T, Q : QueryBuilder> Q.select(expression: () -> Expression<T>): Q = select(expression())

/**
 * Adds [table] to the structure
 */
fun <Q : QueryBuilder> Q.from(table: Table): Q = apply {
    schema.add(QuerySchema.From(table))
}

/**
 * Adds [predicate] to the Query, filtering result set by only rows matching it
 */
fun <Q : QueryBuilder> Q.where(predicate: Expression<Boolean>): Q = apply {
    filter.add(predicate)
}

fun <Q : QueryBuilder> Q.where(predicate: () -> Expression<Boolean>): Q = where(predicate())



