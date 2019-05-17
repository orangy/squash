package org.jetbrains.squash.expressions

import java.math.BigDecimal

interface FunctionExpression<out R> : Expression<R>

/**
 * Represents any function with a name, single argument, and return value.
 */
class GeneralFunctionExpression<T>(
		val name:String,
		val value:Expression<*>
) : FunctionExpression<T>

class CountExpression(val value: Expression<*>? = null) : FunctionExpression<Long>
class CountDistinctExpression(val value:Expression<*>? = null) : FunctionExpression<Long>

fun Expression<*>.count() = CountExpression(this)
fun Expression<*>.countDistinct() = CountDistinctExpression(this)
fun Expression<*>.min() = GeneralFunctionExpression<Long>("MIN",this)
fun Expression<*>.max() = GeneralFunctionExpression<Long>("MAX", this)
fun Expression<*>.sum() = GeneralFunctionExpression<Long>("SUM",this)
fun Expression<*>.average() = GeneralFunctionExpression<BigDecimal>("AVG",this)
