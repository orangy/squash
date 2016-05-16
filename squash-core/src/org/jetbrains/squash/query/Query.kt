package org.jetbrains.squash.query

import org.jetbrains.squash.expressions.*

interface Query {
    val schema: List<QuerySchema>
    val selection: List<Expression<*>>
    val filter: List<Expression<Boolean>>
}
