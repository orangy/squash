package org.jetbrains.squash.graph

import org.jetbrains.squash.definition.*

class TransactionReferenceLink<TReference>(override val from: TransactionNode<*>,
                                           override val to: TransactionNode<TReference>,
                                           val linkColumn: Column<TReference>,
                                           val propertyName: String) : GraphReference<TransactionProcess> {

    override suspend fun resolveStubs(process: TransactionProcess, fromStubs: List<GraphStub<TransactionProcess, *, *>>) {
        val ids = fromStubs.mapNotNull { it.dataValue(linkColumn.name.id, linkColumn.type.runtimeType.java) }
                .toSet() as Set<TReference>

        if (ids.isNotEmpty()) {
            val toStubs = to.fetchIdentities(process, ids)
            for (fromStub in fromStubs) {
                val parentId = fromStub.dataValue(linkColumn.name.id, linkColumn.type.runtimeType.java) as TReference
                fromStub.references!!.put(propertyName, toStubs.filter { it.id == parentId })
            }
        } else {
            for (fromStub in fromStubs) {
                fromStub.references!!.put(propertyName, emptyList())
            }
        }
    }
}