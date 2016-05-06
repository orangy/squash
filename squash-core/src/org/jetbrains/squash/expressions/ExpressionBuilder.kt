package org.jetbrains.squash.expressions

import kotlin.internal.*

infix fun <T> Expression<@Exact T>.eq(other: Expression<T>): Expression<Boolean> = EqExpression(this, other)
infix fun <T> Expression<@Exact T>.eq(literal: T): Expression<Boolean> = EqExpression(this, LiteralExpression(literal))

infix fun <T> Expression<@Exact T>.neq(other: Expression<T>): Expression<Boolean> = NotEqExpression(this, other)
infix fun <T> Expression<@Exact T>.neq(literal: T): Expression<Boolean> = NotEqExpression(this, LiteralExpression(literal))

infix fun <T> Expression<@Exact T>.lt(other: Expression<T>): Expression<Boolean> = LessExpression(this, other)
infix fun <T> Expression<@Exact T>.lteq(other: Expression<T>): Expression<Boolean> = LessEqExpression(this, other)

infix fun <T> Expression<@Exact T>.gt(literal: T): Expression<Boolean> = GreaterExpression(this, LiteralExpression(literal))
infix fun <T> Expression<@Exact T>.gteq(literal: T): Expression<Boolean> = GreaterEqExpression(this, LiteralExpression(literal))

infix operator fun <T> Expression<@Exact T>.plus(other: Expression<T>): Expression<T> = PlusExpression(this, other)
infix operator fun <T> Expression<@Exact T>.plus(literal: T): Expression<T> = PlusExpression(this, LiteralExpression(literal))

infix operator fun <T> Expression<@Exact T>.minus(other: Expression<T>): Expression<T> = MinusExpression(this, other)
infix operator fun <T> Expression<@Exact T>.minus(literal: T): Expression<T> = MinusExpression(this, LiteralExpression(literal))

infix operator fun <T> Expression<@Exact T>.times(other: Expression<T>): Expression<T> = MultiplyExpression(this, other)
infix operator fun <T> Expression<@Exact T>.times(literal: T): Expression<T> = MultiplyExpression(this, LiteralExpression(literal))

infix operator fun <T> Expression<@Exact T>.div(other: Expression<T>): Expression<T> = DivideExpression(this, other)
infix operator fun <T> Expression<@Exact T>.div(literal: T): Expression<T> = DivideExpression(this, LiteralExpression(literal))

fun <T> literal(literal: T): Expression<T> = LiteralExpression(literal)
fun <T> subquery(body: Query.() -> Unit): Expression<T> = SubQueryExpression(query.apply(body))
