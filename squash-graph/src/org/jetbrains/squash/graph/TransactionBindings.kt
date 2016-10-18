package org.jetbrains.squash.graph

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.statements.*
import java.lang.reflect.*
import kotlin.reflect.*
import kotlin.reflect.jvm.*

class TransactionBindings {
    val typeMap = mutableMapOf<KClass<*>, TransactionBindingsNode<*, *>>()

    fun <T : Any, TReference> bind(type: KClass<T>, identityColumn: Column<TReference>): TransactionBindingsNode<T, TReference> {
        // TODO: if already registered, check identity column
        return typeMap.getOrPut(type) {
            TransactionBindingsNode(this, type, identityColumn)
        } as TransactionBindingsNode<T, TReference>
    }

    inline fun <reified T : Any> bind(identityColumn: Column<*>, configure: TransactionBindingsNode<T, *>.() -> Unit) = bind(T::class, identityColumn).apply(configure)
    inline fun <reified T : Any> bind(identityColumn: Column<*>) = bind(T::class, identityColumn)
}

class TransactionBindingsNode<T : Any, TKey>(val bindings: TransactionBindings, type: KClass<T>, identityColumn: Column<TKey>) : TransactionNode<TKey>(type, identityColumn)

fun bindings(configure: TransactionBindings.() -> Unit) = TransactionBindings().apply(configure)

fun <T : Any, TInstance> KProperty1<T, TInstance>.getBindingType(): KClass<out Any> {
    val javaType = returnType.javaType
    val bindingType = (when {
        javaType is Class<*> -> javaType
        javaType is ParameterizedType && javaType.rawType == List::class.java -> {
            val elementType = javaType.actualTypeArguments[0] as Class<*>
            elementType
        }
        else -> error("Cannot handle type $javaType")
    }).kotlin
    return bindingType
}
