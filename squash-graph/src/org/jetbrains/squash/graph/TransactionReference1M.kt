package org.jetbrains.squash.graph

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.query.*
import org.jetbrains.squash.results.*

class TransactionReference1M<TReference>(override val from: TransactionNode<*>,
                                         override val to: TransactionNode<TReference>,
                                         val parentColumn: ReferenceColumn<TReference>,
                                         val referenceName: String) : GraphReference<TransactionProcess> {

    override fun resolveStubs(process: TransactionProcess, fromStubs: List<GraphStub<TransactionProcess, *, *>>) {
        val ids = fromStubs.map { it.id }.toSet()
        if (ids.isNotEmpty()) {
            val rows = query(parentColumn.table)
                    .where { parentColumn within ids }
                    .executeOn(process.transaction)

            val toStubs = to.fetchStubs(process, rows)
            fromStubs.forEach { stub ->
                val parentId = stub.id
                val references = toStubs.filter {
                    it.data!!.columnValue(parentColumn) == parentId
                }
                stub.references!!.put(referenceName, references)
            }
        } else {
            fromStubs.forEach { stub ->
                stub.references!!.put(referenceName, emptyList())
            }
        }
    }
}
