package org.jetbrains.squash.expressions

infix fun <T> Expression<T>.eq(other: Expression<T>): Expression<Boolean> = EqExpression(this, other)
infix fun <T> Expression<T>.eq(literal: T): Expression<Boolean> = EqExpression(this, LiteralExpression(literal))
fun <T> literal(literal: T): Expression<T> = LiteralExpression(literal)

abstract class BinaryExpression<T1, T2, R>(val left: Expression<T1>, val right: Expression<T2>) : Expression<R>

class EqExpression<T>(left: Expression<T>, right: Expression<T>) : BinaryExpression<T, T, Boolean>(left, right)
class LiteralExpression<T>(val literal: T) : Expression<T>
