package org.jetbrains.squash.graph

/**
 * Represents a stub for an instance
 *
 * It goes through several [state]s:
 *
 * - [State.Identified]: initial [state], has an [id]
 * - [State.Fetched]: after [data] was provided to the stub via [fetch] function
 * - [State.Resolved]: after [references] for this stub has been resolved and it can be materialised
 * - [State.Materialized]: mapped [instance] has been created
 *
 */
class GraphStub<TProcess : GraphProcess<TProcess>, TData, TKey>(val id: TKey, val node: GraphNode<TProcess, TData, TKey>) {
    enum class State { Identified, Fetched, Resolved, Materialized }

    var state = State.Identified
    var data: TData? = null
    var references: MutableMap<String, List<GraphStub<TProcess, *, *>>>? = null
    var instance: Any? = null

    fun fetch(source: TData) {
        check(state == State.Identified) { "NodeStub should be in Identified state to provide it with data" }
        data = source
        state = State.Fetched
    }

    fun getOrCreateInstance(process: GraphProcess<TProcess>) = when (state) {
        State.Materialized -> instance
        else -> {
            check(state == State.Resolved) { "NodeStub should be in Resolved state to materialize" }
            state = State.Materialized
            instance = process.instanceFactory(node)(this)
            instance
        }
    }

    fun <T> dataValue(name: String, type: Class<T>): T? {
        check(state != State.Identified) { "NodeStub should be in Fetched state to get property values" }
        return node.dataValue(data!!, name, type) as? T
    }

    override fun toString(): String = "GraphStub($id : ${node.type.simpleName}"
}