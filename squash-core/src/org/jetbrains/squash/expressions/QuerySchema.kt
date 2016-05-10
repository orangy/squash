package org.jetbrains.squash.expressions

import org.jetbrains.squash.definition.*

sealed class QuerySchema(val target: ColumnOwner) {
    class From(table: ColumnOwner) : QuerySchema(table)
    class InnerJoin(target: ColumnOwner, val condition: Expression<Boolean>) : QuerySchema(target)
}