package org.jetbrains.squash.graph

import java.lang.reflect.*
import kotlin.reflect.*

class GNode<TProcess : GProcess<TProcess>, TData, TKey>(val id: (TData) -> TKey,
                                                       val fetch: (TProcess, Set<TKey>) -> Set<TData>,
                                                       val dataValue: (TData, String, Type) -> Any?,
                                                       val type: KClass<*>) {
    val references = mutableMapOf<String, GReference<TProcess>>()

    fun execute(process: TProcess) {
        val stubs = process.stubMap(this)

        // TODO: performance
        // consider splitting stubs into buckets of appropriate state to avoid filtering
        // but take care of performance, moving them between buckets could be costly
        // though they always happen in their entirety, may be more effective data structure is needed
        // like an ArrayList storing stubs, indicies of identified, fetched and properties, and key:index map
        val identified = stubs.values.filter { it.state == GNodeStub.State.Identified }
        if (identified.isNotEmpty()) {
            val ids = identified.map { it.id }.toSet()
            fetchStubs(process, fetch(process, ids))
            // do not fetch references immediately, because other references to this node can produce more
            return
        }

        val fetched = stubs.values.filter { it.state == GNodeStub.State.Fetched }
        if (fetched.isNotEmpty()) {
            fetchReferences(process, fetched)
            return
        }
    }

    private fun getOrCreateStub(process: TProcess, id: TKey): GNodeStub<TProcess, TData, TKey> {
        val stubs = process.stubMap(this)
        val stub = stubs[id]
        if (stub != null)
            return stub
        val newStub = GNodeStub(id, this)
        stubs.put(id, newStub)
        return newStub
    }

    fun fetchIdentities(process: TProcess, ids: Set<TKey>): List<GNodeStub<TProcess, TData, TKey>> {
        process.queueNode(this)
        return ids.map { id -> getOrCreateStub(process, id) }
    }

    fun fetchStubs(process: TProcess, rows: Set<TData>): List<GNodeStub<TProcess, TData, TKey>> {
        var enqueue = false
        val stubs = rows.map { row ->
            val rowId = id(row)
            getOrCreateStub(process, rowId).apply {
                if (state == GNodeStub.State.Identified) {
                    enqueue = true
                    fetch(row)
                }
            }
        }
        if (enqueue)
            process.queueNode(this)
        return stubs
    }

    fun fetchReferences(process: TProcess, stubs: List<GNodeStub<TProcess, TData, TKey>>) {
        val unprocessedStubs = stubs.filter { it.state == GNodeStub.State.Fetched }
        // mark as resolved to avoid repeated processing in loops
        unprocessedStubs.forEach {
            it.state = GNodeStub.State.Resolved
            it.references = mutableMapOf()
        }
        references.values.forEach {
            it.resolveStubs(process, unprocessedStubs)
        }
    }

    override fun toString(): String = "GNode(type=$type)"
}