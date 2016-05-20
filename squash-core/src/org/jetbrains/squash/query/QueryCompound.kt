package org.jetbrains.squash.query

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.expressions.*

sealed class QueryCompound(val table: Table) {
    class From(table: Table) : QueryCompound(table)
    class InnerJoin(target: Table, val condition: Expression<Boolean>) : QueryCompound(target)
    class LeftOuterJoin(target: Table, val condition: Expression<Boolean>) : QueryCompound(target)
    class RightOuterJoin(target: Table, val condition: Expression<Boolean>) : QueryCompound(target)
}

