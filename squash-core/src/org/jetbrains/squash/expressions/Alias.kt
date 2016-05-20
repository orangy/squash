package org.jetbrains.squash.expressions

import org.jetbrains.squash.definition.*

class AliasColumn<V>(val label: Identifier, val column: Column<V>) : Column<V> by column

infix fun <T> Column<T>.alias(label: String): AliasColumn<T> = AliasColumn(Identifier(label), this)

class AliasTable<T : Table>(val label: Identifier, val table: Table) : Table by table

infix fun <T : Table> T.alias(label: String): AliasTable<T> = AliasTable<T>(Identifier(label), this)

infix fun <T> Expression<T>.alias(label: String): NamedExpression<Identifier, T> = AliasExpression(label, this)
class AliasExpression<out T>(id: String, val expression: Expression<T>) : NamedExpression<Identifier, T> {
    override val name: Identifier = Identifier(id)
}