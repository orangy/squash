package org.jetbrains.squash.expressions

infix fun <V> Expression<V>.contains(values: Collection<V>): ArrayInExpression<V> = ArrayInExpression(this, values)
infix fun <V> Expression<V>.containsAny(values: Collection<V>): ArrayOverlapExpression<V> = ArrayOverlapExpression(this, values)
