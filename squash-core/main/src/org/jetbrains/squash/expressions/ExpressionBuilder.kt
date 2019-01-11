package org.jetbrains.squash.expressions

import org.jetbrains.squash.query.*
import org.jetbrains.squash.statements.*
import kotlin.internal.*

infix fun Expression<Boolean>.and(other: Expression<Boolean>): Expression<Boolean> = AndExpression(this, other)
infix fun Expression<Boolean>.or(other: Expression<Boolean>): Expression<Boolean> = OrExpression(this, other)

@JvmName("postfixNot")
fun Expression<Boolean>.not(): Expression<Boolean> = NotExpression(this)
fun not(expression: Expression<Boolean>): Expression<Boolean> = NotExpression(expression)

infix fun <V> Expression<@Exact V?>.eq(other: Expression<V?>): Expression<Boolean> = EqExpression(this, other)
infix fun <V> Expression<@Exact V>.eq(literal: V): Expression<Boolean> = EqExpression(this, LiteralExpression(literal))

infix fun <V> Expression<@Exact V>.neq(other: Expression<V>): Expression<Boolean> = NotEqExpression(this, other)
infix fun <V> Expression<@Exact V>.neq(literal: V): Expression<Boolean> = NotEqExpression(this, LiteralExpression(literal))

infix fun <V> Expression<@Exact V>.lt(other: Expression<V>): Expression<Boolean> = LessExpression(this, other)
infix fun <V> Expression<@Exact V>.lt(literal: V): Expression<Boolean> = LessExpression(this, LiteralExpression(literal))

infix fun <V> Expression<@Exact V>.lteq(other: Expression<V>): Expression<Boolean> = LessEqExpression(this, other)
infix fun <V> Expression<@Exact V>.lteq(literal: V): Expression<Boolean> = LessEqExpression(this, LiteralExpression(literal))

infix fun <V> Expression<@Exact V>.gt(other: Expression<V>): Expression<Boolean> = GreaterExpression(this, other)
infix fun <V> Expression<@Exact V>.gt(literal: V): Expression<Boolean> = GreaterExpression(this, LiteralExpression(literal))

infix fun <V> Expression<@Exact V>.gteq(other: Expression<V>): Expression<Boolean> = GreaterEqExpression(this, other)
infix fun <V> Expression<@Exact V>.gteq(literal: V): Expression<Boolean> = GreaterEqExpression(this, LiteralExpression(literal))

infix operator fun <V> Expression<@Exact V>.plus(other: Expression<V>): Expression<V> = PlusExpression(this, other)
infix operator fun <V> Expression<@Exact V>.plus(literal: V): Expression<V> = PlusExpression(this, LiteralExpression(literal))

infix operator fun <V> Expression<@Exact V>.minus(other: Expression<V>): Expression<V> = MinusExpression(this, other)
infix operator fun <V> Expression<@Exact V>.minus(literal: V): Expression<V> = MinusExpression(this, LiteralExpression(literal))

infix operator fun <V> Expression<@Exact V>.times(other: Expression<V>): Expression<V> = MultiplyExpression(this, other)
infix operator fun <V> Expression<@Exact V>.times(literal: V): Expression<V> = MultiplyExpression(this, LiteralExpression(literal))

infix operator fun <V> Expression<@Exact V>.div(other: Expression<V>): Expression<V> = DivideExpression(this, other)
infix operator fun <V> Expression<@Exact V>.div(literal: V): Expression<V> = DivideExpression(this, LiteralExpression(literal))

infix fun <V> List<V>.contains(value: Expression<V>): InExpression<V> = InExpression(value, this)
infix fun <V> Expression<V>.within(values: Collection<V>): InExpression<V> = InExpression(this, values)

fun <V> literal(value: V) = LiteralExpression(value)
fun <V> subquery(body: QueryBuilder.() -> Unit) = SubQueryExpression<V>(QueryStatement().apply(body))
infix fun Expression<String>.like(literal: String): Expression<Boolean> = LikeExpression(this, LiteralExpression(literal))
