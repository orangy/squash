package org.jetbrains.squash.query

import org.jetbrains.squash.expressions.*

interface Query {
    val compound: List<QueryCompound>
    val selection: List<Expression<*>>
    val filter: List<Expression<Boolean>>
    val order: List<QueryOrder>
    val grouping: List<Expression<*>>
    val having: List<Expression<Boolean>>
    val modifiers : List<QueryModifier>
}
