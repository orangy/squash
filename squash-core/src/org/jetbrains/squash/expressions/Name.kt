package org.jetbrains.squash.expressions

interface Name {
    val id: String
}

data class Identifier(override val id: String) : Name {
    override fun toString(): String = "[$id]"
}

data class QualifiedIdentifier<N : Name>(val parent: N, val identifier: Identifier) : Name {
    override val id: String get() = identifier.id
    override fun toString(): String = "$parent.[$identifier]"
}

interface NamedExpression<N : Name, out T> : Expression<T> {
    val name: N
}

interface FieldCollection {
    val fields: List<Expression<*>>
}