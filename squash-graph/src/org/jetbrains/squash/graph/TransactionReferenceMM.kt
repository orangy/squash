package org.jetbrains.squash.graph

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.query.*
import org.jetbrains.squash.results.*

class TransactionReferenceMM<TReference>(override val from: TransactionNode<*>,
                                         override val to: TransactionNode<TReference>,
                                         val fromColumn: ReferenceColumn<TReference>,
                                         val toColumn: ReferenceColumn<TReference>,
                                         val referenceName: String) : GraphReference<TransactionProcess> {

    override fun resolveStubs(process: TransactionProcess, fromStubs: List<GraphStub<TransactionProcess, *, *>>) {
        val ids = fromStubs.map { it.id }.toSet()
        if (ids.isNotEmpty()) {
            val rows = from(toColumn.reference.compound)
                    .innerJoin(toColumn.compound) {
                        (toColumn.reference eq toColumn) and (fromColumn within ids)
                    }
                    .select(toColumn.reference.compound)
                    .select(fromColumn)
                    .executeOn(process.transaction)

            val children = rows.groupBy { it.columnValue(fromColumn) }
            fromStubs.forEach { stub ->
                val parentId = stub.id
                val parentChildren = children[parentId]
                val references = if (parentChildren != null) {
                    to.fetchStubs(process, parentChildren.asSequence())
                } else
                    emptyList()
                stub.references!!.put(referenceName, references)
            }
        } else {
            fromStubs.forEach { stub ->
                stub.references!!.put(referenceName, emptyList())
            }
        }
    }
}
