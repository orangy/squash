package org.jetbrains.squash.query

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.expressions.*

sealed class QuerySchema(val target: ColumnOwner) {
    class From(table: ColumnOwner) : QuerySchema(table)
    class InnerJoin(target: ColumnOwner, val condition: Expression<Boolean>) : QuerySchema(target)
}