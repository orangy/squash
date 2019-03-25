package org.jetbrains.squash.expressions

class ArrayInExpression<out V>(val value: Expression<V>, val values: Collection<V>) : Expression<Boolean>
class ArrayOverlapExpression<out V>(val value: Expression<V>, val values: Collection<V>) : Expression<Boolean>