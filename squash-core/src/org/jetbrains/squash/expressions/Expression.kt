package org.jetbrains.squash.expressions

abstract class Expression<out T>() {
    private val _hashCode by lazy { toString().hashCode() }

    override fun equals(other: Any?): Boolean {
        return (other as? Expression<*>)?.toString() == toString()
    }

    override fun hashCode(): Int {
        return _hashCode
    }
}
