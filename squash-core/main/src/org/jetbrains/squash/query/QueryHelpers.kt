@file:JvmName("Queries")
@file:JvmMultifileClass

package org.jetbrains.squash.query

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.statements.*

fun from(element: CompoundElement) = QueryStatement().from(element)

fun select(): QueryStatement = QueryStatement()
fun select(vararg expression: Expression<*>): QueryStatement = QueryStatement().select(*expression)
fun select(selector: () -> Expression<*>) = QueryStatement().select(selector)

fun Table.where(selector: () -> Expression<Boolean>) = from(this).where(selector)
fun Table.select(selector: () -> Expression<*>) = from(this).select(selector)
fun Table.select(vararg expression: Expression<*>) = from(this).select(*expression)

fun <Q : QueryBuilder> Q.innerJoin(target: CompoundElement, on: () -> Expression<Boolean>): Q = innerJoin(target, on())
fun <Q : QueryBuilder> Q.leftJoin(target: CompoundElement, on: () -> Expression<Boolean>): Q = leftJoin(target, on())
fun <Q : QueryBuilder> Q.rightJoin(target: CompoundElement, on: () -> Expression<Boolean>): Q = rightJoin(target, on())

fun <Q : QueryBuilder> Q.select(expression: () -> Expression<*>): Q = select(expression())
fun <Q : QueryBuilder> Q.select(element: CompoundElement): Q = apply { selection.add(AllTableColumnsExpression(element)) }
fun <Q : QueryBuilder> Q.where(predicate: () -> Expression<Boolean>): Q = where(predicate())
fun <Q : QueryBuilder> Q.orderBy(ascending: Boolean = true, expression: () -> Expression<*>): Q = orderBy(expression(), ascending)
fun <Q : QueryBuilder> Q.groupBy(expression: () -> Expression<*>): Q = groupBy(expression())
fun <Q : QueryBuilder> Q.having(predicate: () -> Expression<Boolean>): Q = having(predicate())
