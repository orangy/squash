package org.jetbrains.squash.graph

import java.lang.reflect.*
import java.util.*

open class GProcess<TProcess : GProcess<TProcess>> : DynamicAccessor<GNodeStub<TProcess, *, *>> {
    private val queue = LinkedHashSet<GNode<TProcess, *, *>>()
    private val stubs = mutableMapOf<GNode<TProcess, *, *>, MutableMap<*, GNodeStub<TProcess, *, *>>>()
    private val factories = mutableMapOf<GNode<TProcess, *, *>, (GNodeStub<TProcess, *, *>) -> Any>()

    fun <TData, TKey> stubMap(node: GNode<TProcess, TData, TKey>): MutableMap<TKey, GNodeStub<TProcess, TData, TKey>> {
        val value = stubs[node] as? MutableMap<TKey, GNodeStub<TProcess, TData, TKey>>
        if (value != null)
            return value
        val newValue = mutableMapOf<TKey, GNodeStub<TProcess, TData, TKey>>()
        stubs.put(node, newValue as MutableMap<*, GNodeStub<TProcess, *, *>>)
        return newValue
    }

    fun queueNode(node: GNode<TProcess, *, *>) = queue.add(node)

    fun execute() {
        while (queue.isNotEmpty()) {
            val item = queue.first()
            queue.remove(item)
            item.execute(this as TProcess)
        }
    }

    fun instanceFactory(node: GNode<TProcess, *, *>): (GNodeStub<TProcess, *, *>) -> Any {
        return factories.getOrPut(node) {
            val constructor = factoryForInterface(node.type, GNodeStub::class)
            return@getOrPut { stub -> constructor.newInstance(stub, this) }
        }
    }

    override fun getProperty(source: GNodeStub<TProcess, *, *>, name: String, type: Type): Any? {
        val references = source.references!![name] ?: return getPrimitiveProperty(source, name, type)
        return when (type) {
            List::class.java, Collection::class.java, Iterable::class.java -> references.map { it.getOrCreateInstance(this) }
            else -> references.singleOrNull()?.getOrCreateInstance(this)
        }
    }

    private fun getPrimitiveProperty(stub: GNodeStub<TProcess, *, *>, name: String, type: Type): Any? {
        return when (type) {
            is Class<*> -> stub.dataValue(name, type)
            else -> throw UnsupportedOperationException("Cannot bind property $name of type $type in node ${stub.node}")
        }
    }

    override fun callFunction(source: GNodeStub<TProcess, *, *>, name: String, type: Type, values: Array<Any?>): Any? {
        throw UnsupportedOperationException("Unsupported function call $name")
    }

    override fun setProperty(source: GNodeStub<TProcess, *, *>, name: String, type: Type, value: Any?) {
        throw UnsupportedOperationException("Setting property values are not supported")
    }
}