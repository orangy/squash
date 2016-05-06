package org.jetbrains.squash.expressions

interface Name {

}

data class Identifier(val identifier: String) : Name {
    override fun toString(): String = "[$identifier]"
}

interface NamedExpression<N : Name, out T> : Expression<T> {
    val name: N
}

interface FieldCollection {
    val fields: List<Expression<*>>
}