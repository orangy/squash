package org.jetbrains.squash.expressions

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.query.*

class AliasColumn<V>(val label: Identifier, val column: Column<V>) : Column<V> by column

infix fun <T> Column<T>.alias(label: String): AliasColumn<T> = AliasColumn(Identifier(label), this)

class AliasCompoundElement(val label: Identifier, val element: CompoundElement) : CompoundElement by element

infix fun CompoundElement.alias(label: String): AliasCompoundElement = AliasCompoundElement(Identifier(label), this)

infix fun <T> Expression<T>.alias(label: String): NamedExpression<Identifier, T> = AliasExpression(label, this)
class AliasExpression<out T>(id: String, val expression: Expression<T>) : NamedExpression<Identifier, T> {
    override val name: Identifier = Identifier(id)
}