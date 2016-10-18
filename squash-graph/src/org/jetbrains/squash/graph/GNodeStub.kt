package org.jetbrains.squash.graph

class GNodeStub<TProcess : GProcess<TProcess>, TData, TKey>(val id: TKey, val node: GNode<TProcess, TData, TKey>) {
    enum class State { Identified, Fetched, Resolved, Materialized }

    var state = State.Identified
    var data: TData? = null
    var references: MutableMap<String, List<GNodeStub<TProcess, *, *>>>? = null
    var instance: Any? = null

    fun fetch(source: TData) {
        check(state == State.Identified) { "NodeStub should be in Identified state to provide it with data" }
        data = source
        state = State.Fetched
    }

    fun getOrCreateInstance(process: GProcess<TProcess>) = when (state) {
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
}