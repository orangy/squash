package org.jetbrains.squash.expressions

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.query.*

/**
 * Represents an abstract expression of type [R] representable in an SQL request
 */
interface Expression<out R>

abstract class BinaryExpression<out V1, out V2, out R>(val left: Expression<V1>, val right: Expression<V2>) : Expression<R>

class EqExpression<out V>(left: Expression<V>, right: Expression<V>) : BinaryExpression<V, V, Boolean>(left, right)
class NotEqExpression<out V>(left: Expression<V>, right: Expression<V>) : BinaryExpression<V, V, Boolean>(left, right)
class LessExpression<out V>(left: Expression<V>, right: Expression<V>) : BinaryExpression<V, V, Boolean>(left, right)
class LessEqExpression<out V>(left: Expression<V>, right: Expression<V>) : BinaryExpression<V, V, Boolean>(left, right)
class GreaterExpression<out V>(left: Expression<V>, right: Expression<V>) : BinaryExpression<V, V, Boolean>(left, right)
class GreaterEqExpression<out V>(left: Expression<V>, right: Expression<V>) : BinaryExpression<V, V, Boolean>(left, right)

class AndExpression(left: Expression<Boolean>, right: Expression<Boolean>) : BinaryExpression<Boolean, Boolean, Boolean>(left, right)
class OrExpression(left: Expression<Boolean>, right: Expression<Boolean>) : BinaryExpression<Boolean, Boolean, Boolean>(left, right)
class NotExpression(val operand: Expression<Boolean>) : Expression<Boolean>

class PlusExpression<out V>(left: Expression<V>, right: Expression<V>) : BinaryExpression<V, V, V>(left, right)
class MinusExpression<out V>(left: Expression<V>, right: Expression<V>) : BinaryExpression<V, V, V>(left, right)
class MultiplyExpression<out V>(left: Expression<V>, right: Expression<V>) : BinaryExpression<V, V, V>(left, right)
class DivideExpression<out V>(left: Expression<V>, right: Expression<V>) : BinaryExpression<V, V, V>(left, right)

class LiteralExpression<out V>(val literal: V) : Expression<V>
class SubQueryExpression<out V>(val query: Query) : Expression<V>

class LikeExpression(left: Expression<String>, right: LiteralExpression<String>) : BinaryExpression<String, String, Boolean>(left, right)
class InExpression<out V>(val value: Expression<V>, val values: Collection<V>) : Expression<Boolean>
class AllTableColumnsExpression(val table: Table) : Expression<Unit>