package org.jetbrains.squash.expressions

import kotlin.internal.*

infix fun <V> Expression<V>.eq(other: Expression<V>): Expression<Boolean> = EqExpression(this, other)
infix fun <V> Expression<V>.eq(literal: V): Expression<Boolean> = EqExpression(this, LiteralExpression(literal))

infix fun <V> Expression<@Exact V>.neq(other: Expression<V>): Expression<Boolean> = NotEqExpression(this, other)
infix fun <V> Expression<@Exact V>.neq(literal: V): Expression<Boolean> = NotEqExpression(this, LiteralExpression(literal))

infix fun <V> Expression<@Exact V>.lt(other: Expression<V>): Expression<Boolean> = LessExpression(this, other)
infix fun <V> Expression<@Exact V>.lteq(other: Expression<V>): Expression<Boolean> = LessEqExpression(this, other)

infix fun <V> Expression<@Exact V>.gt(literal: V): Expression<Boolean> = GreaterExpression(this, LiteralExpression(literal))
infix fun <V> Expression<@Exact V>.gteq(literal: V): Expression<Boolean> = GreaterEqExpression(this, LiteralExpression(literal))

infix operator fun <V> Expression<@Exact V>.plus(other: Expression<V>): Expression<V> = PlusExpression(this, other)
infix operator fun <V> Expression<@Exact V>.plus(literal: V): Expression<V> = PlusExpression(this, LiteralExpression(literal))

infix operator fun <V> Expression<@Exact V>.minus(other: Expression<V>): Expression<V> = MinusExpression(this, other)
infix operator fun <V> Expression<@Exact V>.minus(literal: V): Expression<V> = MinusExpression(this, LiteralExpression(literal))

infix operator fun <V> Expression<@Exact V>.times(other: Expression<V>): Expression<V> = MultiplyExpression(this, other)
infix operator fun <V> Expression<@Exact V>.times(literal: V): Expression<V> = MultiplyExpression(this, LiteralExpression(literal))

infix operator fun <V> Expression<@Exact V>.div(other: Expression<V>): Expression<V> = DivideExpression(this, other)
infix operator fun <V> Expression<@Exact V>.div(literal: V): Expression<V> = DivideExpression(this, LiteralExpression(literal))

fun <V> literal(value: V): Expression<V> = LiteralExpression(value)
fun <V> subquery(body: QueryBuilder.() -> Unit): Expression<V> = SubQueryExpression(query().apply(body))
