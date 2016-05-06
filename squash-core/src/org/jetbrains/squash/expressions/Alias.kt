package org.jetbrains.squash.expressions

infix fun <T> Expression<T>.alias(name : String) : NamedExpression<Identifier, T> = AliasExpression(name, this)

class AliasExpression<T>(id: String, val expression: Expression<T>) : NamedExpression<Identifier, T> {
    override val name: Identifier = Identifier(id)
}