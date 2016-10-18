package org.jetbrains.squash.graph

import java.lang.reflect.*
import java.util.*
import kotlin.reflect.*

open class GraphProcess<TProcess : GraphProcess<TProcess>>() : DynamicAccessor<GraphStub<TProcess, *, *>> {
    private val queue = LinkedHashSet<GraphNode<TProcess, *, *>>()
    private val stubs = mutableMapOf<GraphNode<TProcess, *, *>, MutableMap<*, GraphStub<TProcess, *, *>>>()
    private val factories = mutableMapOf<GraphNode<TProcess, *, *>, (GraphStub<TProcess, *, *>) -> Any>()

    fun <TData, TKey> stubMap(node: GraphNode<TProcess, TData, TKey>): MutableMap<TKey, GraphStub<TProcess, TData, TKey>> {
        val value = stubs[node] as? MutableMap<TKey, GraphStub<TProcess, TData, TKey>>
        if (value != null)
            return value
        val newValue = mutableMapOf<TKey, GraphStub<TProcess, TData, TKey>>()
        stubs.put(node, newValue as MutableMap<*, GraphStub<TProcess, *, *>>)
        return newValue
    }

    fun queueNode(node: GraphNode<TProcess, *, *>) = queue.add(node)

    fun execute() {
        while (queue.isNotEmpty()) {
            val item = queue.first()
            queue.remove(item)
            item.execute(this as TProcess)
        }
    }

    fun instanceFactory(node: GraphNode<TProcess, *, *>): (GraphStub<TProcess, *, *>) -> Any {
        return factories.getOrPut(node) {
            val constructor = factoryForInterface(node.type, GraphStub::class)
            return@getOrPut { stub -> constructor.newInstance(stub, this) }
        }
    }

    override fun getProperty(source: GraphStub<TProcess, *, *>, name: String, type: Type): Any? {
        val references = source.references!![name] ?: return getPrimitiveProperty(source, name, type)
        return when (type) {
            List::class.java, Collection::class.java, Iterable::class.java -> references.map { it.getOrCreateInstance(this) }
            else -> references.singleOrNull()?.getOrCreateInstance(this)
        }
    }

    private fun getPrimitiveProperty(stub: GraphStub<TProcess, *, *>, name: String, type: Type): Any? {
        return when (type) {
            is Class<*> -> stub.dataValue(name, type)
            else -> throw UnsupportedOperationException("Cannot bind property $name of type $type in node ${stub.node}")
        }
    }

    override fun callFunction(source: GraphStub<TProcess, *, *>, name: String, type: Type, values: Array<Any?>): Any? {
        throw UnsupportedOperationException("Unsupported function call $name")
    }

    override fun setProperty(source: GraphStub<TProcess, *, *>, name: String, type: Type, value: Any?) {
        throw UnsupportedOperationException("Setting property values are not supported")
    }
}