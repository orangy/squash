package org.jetbrains.squash.query

import org.jetbrains.squash.expressions.*

sealed class QueryCompound(val element: CompoundElement) {
    class From(element: CompoundElement) : QueryCompound(element)
    class InnerJoin(element: CompoundElement, val condition: Expression<Boolean>) : QueryCompound(element)
    class LeftOuterJoin(element: CompoundElement, val condition: Expression<Boolean>) : QueryCompound(element)
    class RightOuterJoin(element: CompoundElement, val condition: Expression<Boolean>) : QueryCompound(element)
}

