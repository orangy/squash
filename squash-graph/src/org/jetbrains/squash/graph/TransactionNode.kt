package org.jetbrains.squash.graph

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.query.*
import org.jetbrains.squash.results.*
import java.lang.reflect.*
import kotlin.reflect.*

open class TransactionNode<TKey>(type: KClass<*>, val identityColumn: Column<TKey>)
    : GraphNode<TransactionProcess, ResultRow, TKey>(type) {

    override fun fetch(process: TransactionProcess, keys: Set<TKey>): Set<ResultRow> {
        return query(identityColumn.table).where { identityColumn within keys }.executeOn(process.transaction).toSet()
    }

    override fun id(data: ResultRow): TKey = data.columnValue(identityColumn)

    override fun dataValue(data: ResultRow, name: String, type: Type) = data.columnValue((type as Class<*>).kotlin, name)
}