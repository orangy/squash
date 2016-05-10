package org.jetbrains.squash.expressions

import org.jetbrains.squash.definition.*

open class QueryBuilder : Query {
    override val schema = mutableListOf<QuerySchema>()
    override val selection = mutableListOf<Expression<*>>()
    override val filter = mutableListOf<Expression<*>>()

    override fun <T> select(vararg expression: Expression<T>): Query = apply {
        selection.addAll(expression)
    }

    override fun from(table: Table): Query = apply {
        schema.add(QuerySchema.From(table))
    }

    override fun innerJoin(target: Table, on: Expression<Boolean>): Query = apply {
        schema.add(QuerySchema.InnerJoin(target, on))
    }

    override fun where(predicate: Expression<Boolean>): Query = apply {
        filter.add(predicate)
    }
}