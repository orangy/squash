package org.jetbrains.squash.expressions

/**
 * Represents an abstract expression of type [T] representable in an SQL request
 */
interface Expression<out T>

abstract class BinaryExpression<T1, T2, R>(val left: Expression<T1>, val right: Expression<T2>) : Expression<R>

class EqExpression<T>(left: Expression<T>, right: Expression<T>) : BinaryExpression<T, T, Boolean>(left, right)
class NotEqExpression<T>(left: Expression<T>, right: Expression<T>) : BinaryExpression<T, T, Boolean>(left, right)
class LessExpression<T>(left: Expression<T>, right: Expression<T>) : BinaryExpression<T, T, Boolean>(left, right)
class LessEqExpression<T>(left: Expression<T>, right: Expression<T>) : BinaryExpression<T, T, Boolean>(left, right)
class GreaterExpression<T>(left: Expression<T>, right: Expression<T>) : BinaryExpression<T, T, Boolean>(left, right)
class GreaterEqExpression<T>(left: Expression<T>, right: Expression<T>) : BinaryExpression<T, T, Boolean>(left, right)

class AndExpression(left: Expression<Boolean>, right: Expression<Boolean>) : BinaryExpression<Boolean, Boolean, Boolean>(left, right)
class OrExpression(left: Expression<Boolean>, right: Expression<Boolean>) : BinaryExpression<Boolean, Boolean, Boolean>(left, right)
class NotExpression(val operand: Expression<Boolean>) : Expression<Boolean>

class PlusExpression<T>(left: Expression<T>, right: Expression<T>) : BinaryExpression<T, T, T>(left, right)
class MinusExpression<T>(left: Expression<T>, right: Expression<T>) : BinaryExpression<T, T, T>(left, right)
class MultiplyExpression<T>(left: Expression<T>, right: Expression<T>) : BinaryExpression<T, T, T>(left, right)
class DivideExpression<T>(left: Expression<T>, right: Expression<T>) : BinaryExpression<T, T, T>(left, right)

class LiteralExpression<T>(val literal: T) : Expression<T>
class SubQueryExpression<T>(val query: Query) : Expression<T>