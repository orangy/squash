package org.jetbrains.squash.graph

/**
 * Link between [from] node and [to] node
 */
interface GraphReference<TProcess : GraphProcess<TProcess>> {
    val from: GraphNode<TProcess, *, *>
    val to: GraphNode<TProcess, *, *>

    suspend fun resolveStubs(process: TProcess, fromStubs: List<GraphStub<TProcess, *, *>>)
}