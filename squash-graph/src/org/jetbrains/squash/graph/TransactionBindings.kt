package org.jetbrains.squash.graph

import org.jetbrains.squash.definition.*
import java.lang.reflect.*
import kotlin.reflect.*
import kotlin.reflect.jvm.*

class TransactionBindings {
    val bindingMap = mutableMapOf<KClass<*>, TransactionBindingsNode<*>>()

    fun <TReference> bind(type: KClass<*>, identityColumn: Column<TReference>): TransactionBindingsNode<TReference> {
        // TODO: if already registered, check identity column
        return bindingMap.getOrPut(type) {
            TransactionBindingsNode(this, listOf(type), identityColumn)
        } as TransactionBindingsNode<TReference>
    }

    inline fun <reified T : Any, reified TExtend : Any> extend(configure: TransactionBindingsNode<*>.() -> Unit) {
        val type = T::class
        val node = bindingMap[type] ?: error("Binding for type $type not found")
        val extendType = TExtend::class
        bindingMap[extendType] = node
        node.types.add(extendType)
        node.apply(configure)
    }

    inline fun <reified T : Any> bind(identityColumn: Column<*>, configure: TransactionBindingsNode<*>.() -> Unit) = bind(T::class, identityColumn).apply(configure)
    inline fun <reified T : Any> bind(identityColumn: Column<*>) = bind(T::class, identityColumn)

    inline fun <reified T : Any> import(from: TransactionBindings) {
        val type = T::class
        val importFrom = from.bindingMap[type] ?: error("Binding for type $type not found")
        val importTo = bindingMap.getOrPut(type) { TransactionBindingsNode(this, listOf(type), importFrom.identityColumn) }
        importTo.references.putAll(importFrom.references)
    }
}

class TransactionBindingsNode<TKey>(val bindings: TransactionBindings, types: List<KClass<*>>, identityColumn: Column<TKey>)
    : TransactionNode<TKey>(types, identityColumn)

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

var lastId = 0
val ids = mutableMapOf<TransactionBindingsNode<*>, Int>()
fun id(node: TransactionBindingsNode<*>) = ids.getOrPut(node) { ++lastId }

fun println(indent: String, node: TransactionBindingsNode<*>) {
    println("$indent$node [${id(node)}]")
    node.references.forEach {
        val toNode = it.value.to as TransactionBindingsNode<*>
        println("$indent  ${it.key} : ${it.value.javaClass.simpleName} [${id(toNode)}]")
    }
}

fun println(bindings: TransactionBindings) {
    bindings.bindingMap.values.forEach {
        println("", it)
    }
}