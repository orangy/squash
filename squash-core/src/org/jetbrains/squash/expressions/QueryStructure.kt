package org.jetbrains.squash.expressions

sealed class QueryStructure(val target: FieldCollection) {
    class From(table: FieldCollection) : QueryStructure(table)
    class InnerJoin(target: FieldCollection, val condition: Expression<Boolean>) : QueryStructure(target)
}