package org.jetbrains.squash.expressions

import org.jetbrains.squash.*

class QueryBuilder : Query {
    override val selection = mutableListOf<Expression<*>>()
    override val filter = mutableListOf<Expression<*>>()
    override val structure = mutableListOf<QueryStructure>()

    override fun <T> select(expression: () -> Expression<T>): Query = apply {
        selection.add(expression())
    }

    override fun from(table: Table): Query = apply {
        structure.add(QueryStructure.From(table))
    }

    override fun where(predicate: () -> Expression<Boolean>): Query = apply {
        filter.add(predicate())
    }

    override fun innerJoin(target: Table, on: () -> Expression<Boolean>): Query = apply {
        structure.add(QueryStructure.InnerJoin(target, on()))
    }
}