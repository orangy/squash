package org.jetbrains.squash.expressions

import org.jetbrains.squash.*

class QueryBuilder : Query {
    override val selection = mutableListOf<Expression<*>>()
    override val filter = mutableListOf<Expression<*>>()
    override val structure = mutableListOf<Table>()

    override fun <T> select(expression: () -> Expression<T>): Query = apply {
        selection.add(expression())
    }

    override fun from(table: Table): Query = apply {
        structure.add(table)
    }

    override fun where(predicate: () -> Expression<Boolean>): Query = apply {
        filter.add(predicate())
    }

    fun join(joinType: JoinType, joinTarget: Table, on: () -> Expression<Boolean>): Query = apply {
        //JoinExpression(joinType, joinTarget, on)
    }
}