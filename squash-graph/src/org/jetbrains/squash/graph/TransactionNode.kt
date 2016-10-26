package org.jetbrains.squash.graph

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.query.*
import org.jetbrains.squash.results.*
import java.lang.reflect.*
import kotlin.reflect.*

open class TransactionNode<TKey>(types: List<KClass<*>>, val identityColumn: Column<TKey>)
    : GraphNode<TransactionProcess, ResultRow, TKey>(types) {

    override fun fetch(process: TransactionProcess, keys: Set<TKey>): Sequence<ResultRow> {
        return query(identityColumn.table).where { identityColumn within keys }.executeOn(process.transaction)
    }

    override fun id(data: ResultRow): TKey {
        return data.columnValue(identityColumn)
    }

    override fun dataValue(data: ResultRow, name: String, type: Type): Any? {
        return data.columnValue((type as Class<*>).kotlin, name)
    }
}