package org.jetbrains.squash.graph

import java.lang.reflect.*
import kotlin.reflect.*

/**
 * Node in a graph representing an object of specific [types]
 *
 * Each node may have [references] to other nodes
 * During the [GraphProcess] nodes will be called [execute] method to process their stubs from state to state
 * @param TProcess type of [GraphProcess] for this node
 * @param TData type of data this node is using as a source
 * @param TKey type of key values
 */
abstract class GraphNode<TProcess : GraphProcess<TProcess>, TData, TKey>(types: List<KClass<*>>) {
    val types = types.toMutableList()
    val references = mutableMapOf<String, GraphReference<TProcess>>()

    /**
     * Fetches set of [TData] instances for specified [keys] from the associated source
     */
    protected abstract suspend fun fetch(process: TProcess, keys: Set<TKey>): Sequence<TData>

    /**
     * Gets identity value of [TKey] type from [data]
     */
    protected abstract fun id(data: TData): TKey

    /**
     * Gets value from [data] for property [name] and [type]
     */
    abstract fun dataValue(data: TData, name: String, type: Type): Any?

    /**
     * Pushes stubs from the [process] to the next state
     */
    suspend fun execute(process: TProcess) {
        val stubs = process.stubMap(this)

        // TODO: performance
        // consider splitting stubs into buckets of appropriate state to avoid filtering
        // but take care of performance, moving them between buckets could be costly
        // though they always happen in their entirety, may be more effective data structure is needed
        // like an ArrayList storing stubs, indicies of identified, fetched and properties, and key:index map
        val identified = stubs.values.filter { it.state == GraphStub.State.Identified }
        if (identified.isNotEmpty()) {
            val ids = identified.map { it.id }.toSet()
            fetchStubs(process, fetch(process, ids))
            // do not fetch references immediately, because other references to this node can produce more
            return
        }

        val fetched = stubs.values.filter { it.state == GraphStub.State.Fetched }
        if (fetched.isNotEmpty()) {
            fetchReferences(process, fetched)
            return
        }
    }

    private fun getOrCreateStub(process: TProcess, id: TKey): GraphStub<TProcess, TData, TKey> {
        val stubs = process.stubMap(this)
        val stub = stubs[id]
        if (stub != null)
            return stub
        val newStub = GraphStub(id, this)
        stubs.put(id, newStub)
        return newStub
    }

    fun fetchIdentities(process: TProcess, ids: Collection<TKey>): List<GraphStub<TProcess, TData, TKey>> {
        process.queueNode(this)
        return ids.map { id -> getOrCreateStub(process, id) }
    }

    fun fetchStubs(process: TProcess, rows: Sequence<TData>): List<GraphStub<TProcess, TData, TKey>> {
        var enqueue = false
        val stubs = rows.map { row ->
            val rowId = id(row)
            getOrCreateStub(process, rowId).apply {
                if (state == GraphStub.State.Identified) {
                    enqueue = true
                    fetch(row)
                }
            }
        }.toList()
        if (enqueue)
            process.queueNode(this)
        return stubs
    }

    suspend fun fetchReferences(process: TProcess, stubs: List<GraphStub<TProcess, TData, TKey>>) {
        val unprocessedStubs = stubs.filter { it.state == GraphStub.State.Fetched }
        // mark as resolved to avoid repeated processing in loops
        unprocessedStubs.forEach {
            it.state = GraphStub.State.Resolved
            it.references = mutableMapOf()
        }
        references.values.forEach {
            it.resolveStubs(process, unprocessedStubs)
        }
    }

    override fun toString(): String = "GraphNode(${types.joinToString { it.simpleName!! }})"
}