package org.jetbrains.squash.graph

interface GReference<TProcess : GProcess<TProcess>> {
    val from: GNode<TProcess, *, *>
    val to: GNode<TProcess, *, *>
    fun resolveStubs(process: TProcess, fromStubs: List<GNodeStub<TProcess, *, *>>)
}