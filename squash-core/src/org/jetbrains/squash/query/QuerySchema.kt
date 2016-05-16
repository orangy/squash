package org.jetbrains.squash.query

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.expressions.*

sealed class QuerySchema(val table: Table) {
    class From(table: Table) : QuerySchema(table)
    class InnerJoin(target: Table, val condition: Expression<Boolean>) : QuerySchema(target)
    class LeftOuterJoin(target: Table, val condition: Expression<Boolean>) : QuerySchema(target)
    class RightOuterJoin(target: Table, val condition: Expression<Boolean>) : QuerySchema(target)
}

