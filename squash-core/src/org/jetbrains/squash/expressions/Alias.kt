package org.jetbrains.squash.expressions

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.query.*

class AliasColumn<V>(val label: Identifier, val column: Column<V>) : Column<V> by column

class ColumnInAlias<V>(override val compound: AliasCompoundElement, val column: Column<V>) : Column<V> {
    override val type: ColumnType get() = column.type
    override val name: Name = QualifiedIdentifier<Name>(compound.label, Identifier(column.name.id))
    override val properties: List<ColumnProperty> get() = column.properties
}

class AliasCompoundElement(val label: Identifier, val element: CompoundElement) : CompoundElement by element

class AliasExpression<out T>(id: String, val expression: Expression<T>) : NamedExpression<Identifier, T> {
    override val name: Identifier = Identifier(id)
}

infix fun <T> Column<T>.alias(label: String): AliasColumn<T> = AliasColumn(Identifier(label), this)
infix fun CompoundElement.alias(label: String): AliasCompoundElement = AliasCompoundElement(Identifier(label), this)
infix fun <T> Expression<T>.alias(label: String): NamedExpression<Identifier, T> = AliasExpression(label, this)

operator fun <T> Column<T>.invoke(aliasCompoundElement: AliasCompoundElement) = ColumnInAlias(aliasCompoundElement, this)
