@file:JvmName("Queries")
@file:JvmMultifileClass

package org.jetbrains.squash.query

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.statements.*

fun query(table: Table) = query().from(table)

fun Table.where(selector: () -> Expression<Boolean>) = query(this).where(selector)
fun Table.select(selector: () -> Expression<*>) = query(this).select(selector)
fun Table.select(vararg expression: Expression<*>) = query(this).select(*expression)

fun <Q : QueryBuilder> Q.innerJoin(target: Table, on: () -> Expression<Boolean>): Q = innerJoin(target, on())
fun <Q : QueryBuilder> Q.select(expression: () -> Expression<*>): Q = select(expression())
fun <Q : QueryBuilder> Q.where(predicate: () -> Expression<Boolean>): Q = where(predicate())
fun <Q : QueryBuilder> Q.orderBy(ascending: Boolean = true, expression: () -> Expression<*>): Q = orderBy(expression(), ascending)
fun <Q : QueryBuilder> Q.groupBy(expression: () -> Expression<*>): Q = groupBy(expression())
fun <Q : QueryBuilder> Q.having(predicate: () -> Expression<Boolean>): Q = having(predicate())
