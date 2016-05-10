package org.jetbrains.squash.expressions

import org.jetbrains.squash.*

infix fun <T> Expression<T>.alias(name : String) : NamedExpression<Identifier, T> = AliasExpression(name, this)

class AliasExpression<out T>(id: String, val expression: Expression<T>) : NamedExpression<Identifier, T> {
    override val name: Identifier = Identifier(id)
}