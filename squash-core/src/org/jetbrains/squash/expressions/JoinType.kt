package org.jetbrains.squash.expressions

import org.jetbrains.squash.*

sealed class QueryStructure(val target: FieldCollection) {
    class From(table: FieldCollection) : QueryStructure(table)
    class InnerJoin(target: FieldCollection, val condition: Expression<Boolean>) : QueryStructure(target)
}