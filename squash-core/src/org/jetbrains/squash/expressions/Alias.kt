package org.jetbrains.squash.expressions

import org.jetbrains.squash.*
import org.jetbrains.squash.definition.*

class AliasColumn<V>(val label: Identifier, val column: Column<V>) : Column<V> by column
infix fun <T> Column<T>.alias(name: String): AliasColumn<T> = AliasColumn(Identifier(name), this)

class AliasTable<T : Table>(val name: Name, val table: Table) : Table by table
infix fun <T : Table> T.alias(name: String): AliasTable<T> = AliasTable<T>(Identifier(name), this)

infix fun <T> Expression<T>.alias(name: String): NamedExpression<Identifier, T> = AliasExpression(name, this)
class AliasExpression<out T>(id: String, val expression: Expression<T>) : NamedExpression<Identifier, T> {
    override val name: Identifier = Identifier(id)
}