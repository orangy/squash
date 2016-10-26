package org.jetbrains.squash.graph

import org.jetbrains.squash.connection.*
import org.jetbrains.squash.definition.*
import org.jetbrains.squash.statements.*
import kotlin.reflect.*

class TransactionQueryBinding<T : Any>(val query: QueryStatement,
                                       val node: TransactionBindingsNode<*>) : TransactionExecutable<List<T>> {
    override fun executeOn(transaction: Transaction): List<T> {
        val result = query.executeOn(transaction)
        val process = TransactionProcess(transaction)
        val stubs = node.fetchStubs(process, result) // TODO: make use of sequences
        process.queueNode(node)
        process.execute()
        return stubs.map { it.getOrCreateInstance(process) as T }
    }
}

fun <T : Any> QueryStatement.bind(type: KClass<T>, identityColumn: Column<*>): TransactionQueryBinding<T> {
    val bindings = TransactionBindings()
    val node = bindings.bind(type, identityColumn)
    val queryBinding = TransactionQueryBinding<T>(this, node)
    return queryBinding
}

inline fun <reified T : Any> QueryStatement.bind(bindings: TransactionBindings): TransactionQueryBinding<T> {
    val node = bindings.bindingMap[T::class] as TransactionBindingsNode<*>
    val queryBinding = TransactionQueryBinding<T>(this, node)
    return queryBinding
}

inline fun <reified T : Any> QueryStatement.bind(identityColumn: Column<*>): TransactionQueryBinding<T> {
    return bind(identityColumn, {})
}

inline fun <reified T : Any> QueryStatement.bind(table: Table): TransactionQueryBinding<T> {
    val identityColumn = table.constraints.primaryKey!!.columns.single()
    return bind(identityColumn, {})
}

inline fun <reified T : Any> QueryStatement.bind(table: Table, configure: TransactionBindingsNode<*>.() -> Unit): TransactionQueryBinding<T> {
    val identityColumn = table.constraints.primaryKey!!.columns.single()
    return bind(identityColumn, configure)
}

inline fun <reified T : Any> QueryStatement.bind(identityColumn: Column<*>, configure: TransactionBindingsNode<*>.() -> Unit): TransactionQueryBinding<T> {
    return bind(T::class, identityColumn).apply {
        node.configure()
    }
}
