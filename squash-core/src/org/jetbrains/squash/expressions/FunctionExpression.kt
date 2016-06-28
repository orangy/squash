package org.jetbrains.squash.expressions

interface FunctionExpression<out R> : Expression<R>

class CountExpression(val value: Expression<*>) : FunctionExpression<Long>
class CountDistinctExpression(val value: Expression<*>) : FunctionExpression<Long>
class MinExpression(val value: Expression<*>) : FunctionExpression<Long>
class MaxExpression(val value: Expression<*>) : FunctionExpression<Long>
class SumExpression(val value: Expression<*>) : FunctionExpression<Long>

fun Expression<*>.count() = CountExpression(this)
fun Expression<*>.countDistinct() = CountDistinctExpression(this)
fun Expression<*>.min() = MinExpression(this)
fun Expression<*>.max() = MaxExpression(this)
fun Expression<*>.sum() = SumExpression(this)

