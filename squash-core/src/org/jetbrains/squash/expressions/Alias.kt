package org.jetbrains.squash.expressions

import org.jetbrains.squash.*
import org.jetbrains.squash.definition.*

infix fun <T> Expression<T>.alias(name: String): NamedExpression<Identifier, T> = AliasExpression(name, this)
infix fun <T> Column<T>.alias(name: String): AliasColumn<T> = AliasColumn(Identifier(name), this)

class AliasColumn<V>(val label: Identifier, val column: Column<V>) : Column<V> by column

class AliasExpression<out T>(id: String, val expression: Expression<T>) : NamedExpression<Identifier, T> {
    override val name: Identifier = Identifier(id)
}