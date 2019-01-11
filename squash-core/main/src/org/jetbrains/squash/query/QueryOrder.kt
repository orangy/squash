package org.jetbrains.squash.query

import org.jetbrains.squash.expressions.*

sealed class QueryOrder(val expression: Expression<*>) {
    class Ascending(expression: Expression<*>) : QueryOrder(expression)
    class Descending(expression: Expression<*>) : QueryOrder(expression)
}