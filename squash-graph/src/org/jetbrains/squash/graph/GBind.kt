package org.jetbrains.squash.graph

import org.jetbrains.squash.connection.*
import org.jetbrains.squash.definition.*
import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.query.*
import org.jetbrains.squash.results.*
import org.jetbrains.squash.statements.*
import java.lang.reflect.*
import kotlin.reflect.*
import kotlin.reflect.jvm.*

class TransactionProcess(val transaction: Transaction) : GProcess<TransactionProcess>()

class GBind<T : Any>(val binding: GBinding<T, *>, val query: QueryStatement) : TransactionExecutable<List<T>> {
    override fun executeOn(transaction: Transaction): List<T> {
        return binding.execute(transaction, query)
    }
}

inline fun <reified T : Any, TKey> binding(identityColumn: Column<TKey>) = GBinding(T::class, identityColumn)
inline fun <reified T : Any, TKey> binding(identityColumn: Column<TKey>, configure: GBinding<T, TKey>.() -> Unit): GBinding<T, TKey> {
    return GBinding(T::class, identityColumn).apply(configure)
}

class GBinding<T : Any, TKey>(val type: KClass<T>, identityColumn: Column<TKey>) {
    val node = createNode(identityColumn, type)

    fun execute(transaction: Transaction, query: QueryStatement): List<T> {
        val result = query.executeOn(transaction)
        val process = TransactionProcess(transaction)
        val stubs = node.fetchStubs(process, result.toSet()) // TODO: make use of sequences
        process.queueNode(node)
        process.execute()
        return stubs.map { it.getOrCreateInstance(process) as T }
    }
}

fun <TColumn> createNode(identityColumn: Column<TColumn>, type: KClass<*>): GNode<TransactionProcess, ResultRow, TColumn> {
    val id = { row: ResultRow -> row.columnValue(identityColumn) }
    val fetch = { process: TransactionProcess, ids: Set<*> ->
        query(identityColumn.table).where { identityColumn within ids }.executeOn(process.transaction).toSet()
    }
    val property = { row: ResultRow, name: String, type: Type ->
        row.columnValue((type as Class<*>).kotlin, name)
    }
    return GNode(id, fetch, property, type)
}

inline fun <reified T : Any> QueryStatement.bind(table: TableDefinition, noinline build: GBinding<T, Any?>.() -> Unit = {}): GBind<T> = bind(T::class, table, build)
inline fun <reified T : Any, TColumn> QueryStatement.bind(column: Column<TColumn>, noinline build: GBinding<T, TColumn>.() -> Unit = {}): GBind<T> = bind(T::class, column.table, build)

fun <T : Any, TColumn> QueryStatement.bind(type: KClass<T>, table: Table, build: GBinding<T, TColumn>.() -> Unit = {}): GBind<T> {
    val keyColumn = table.constraints.primaryKey!!.columns.single() as Column<TColumn>
    val binding = GBinding(type, keyColumn)
    return binding.apply(build).bind(this)
}

private fun <TKey, T : Any> GBinding<T, TKey>.bind(queryStatement: QueryStatement): GBind<T> {
    return GBind(this, queryStatement)
}

fun <T : Any, TInstance, TReference> GBinding<T, *>.reference(property: KProperty1<T, TInstance>, referenceColumn: ReferenceColumn<TReference>) {
    val targetColumn = referenceColumn.reference
    val propertyType = (property.returnType.javaType as Class<*>).kotlin
    val toNode = createNode(targetColumn, propertyType)
    reference(toNode, property, referenceColumn)
}

fun <T : Any, TInstance, TReference> GBinding<T, *>.reference(toNode: GNode<TransactionProcess, ResultRow, TReference>, property: KProperty1<T, TInstance>, referenceColumn: ReferenceColumn<TReference>) {
    val reference = object : GReference<TransactionProcess> {
        override val from: GNode<TransactionProcess, ResultRow, *> = node
        override val to: GNode<TransactionProcess, ResultRow, TReference> = toNode

        override fun resolveStubs(process: TransactionProcess, fromStubs: List<GNodeStub<TransactionProcess, *, *>>) {
            val ids = fromStubs.map { it.dataValue(referenceColumn.name.id, referenceColumn.type.runtimeType.java) as TReference }
                    .filter { it != null }
                    .toSet()
            if (ids.isNotEmpty()) {
                val toStubs = to.fetchIdentities(process, ids)
                fromStubs.forEach { stub ->
                    val parentId = stub.dataValue(referenceColumn.name.id, referenceColumn.type.runtimeType.java) as TReference
                    stub.references!!.put(property.name, toStubs.filter { it.id == parentId })
                }
            } else {
                fromStubs.forEach { stub ->
                    stub.references!!.put(property.name, emptyList())
                }
            }
        }
    }
    node.references.put(property.name, reference)
}

fun <T : Any, TInstance, TReference> GBinding<T, *>.references(property: KProperty1<T, TInstance>, fromColumn: ReferenceColumn<TReference>, toColumn: ReferenceColumn<TReference>) {
    val targetColumn = toColumn.reference
    val javaType = property.returnType.javaType
    val bindingType = (when {
        javaType is Class<*> -> javaType
        javaType is ParameterizedType && javaType.rawType == List::class.java -> {
            val elementType = javaType.actualTypeArguments[0] as Class<*>
            elementType
        }
        else -> error("Cannot handle type $javaType")
    }).kotlin
    val toNode = createNode(targetColumn, bindingType)

    references(toNode, property, fromColumn, toColumn)
}

fun <T : Any, TInstance, TReference> GBinding<T, *>.references(toNode: GNode<TransactionProcess, ResultRow, TReference>,
                                                               property: KProperty1<T, TInstance>,
                                                               fromColumn: ReferenceColumn<TReference>,
                                                               toColumn: ReferenceColumn<TReference>) {
    val reference = object : GReference<TransactionProcess> {
        override val from: GNode<TransactionProcess, ResultRow, *> = node
        override val to: GNode<TransactionProcess, ResultRow, TReference> = toNode

        override fun resolveStubs(process: TransactionProcess, fromStubs: List<GNodeStub<TransactionProcess, *, *>>) {
            val ids = fromStubs.map { it.id }.toSet()
            if (ids.isNotEmpty()) {
                val rows = query(toColumn.reference.table)
                        .innerJoin(toColumn.table) {
                            (toColumn.reference eq toColumn) and (fromColumn within ids)
                        }
                        .select(toColumn.reference.table)
                        .select(fromColumn)
                        .executeOn(process.transaction).toSet()

                val toStubs = to.fetchStubs(process, rows)
                fromStubs.forEach { stub ->
                    val parentId = stub.id
                    val references = toStubs.filter {
                        it.data!!.columnValue(fromColumn) == parentId
                    }
                    stub.references!!.put(property.name, references)
                }
            } else {
                fromStubs.forEach { stub ->
                    stub.references!!.put(property.name, emptyList())
                }
            }
        }
    }
    node.references.put(property.name, reference)
}

fun <T : Any, TInstance, TReference> GBinding<T, *>.references(property: KProperty1<T, TInstance>, parentColumn: ReferenceColumn<TReference>) {
    val javaType = property.returnType.javaType
    val bindingType = (when {
        javaType is Class<*> -> javaType
        javaType is ParameterizedType && javaType.rawType == List::class.java -> {
            val elementType = javaType.actualTypeArguments[0] as Class<*>
            elementType
        }
        else -> error("Cannot handle type $javaType")
    }).kotlin
    val keyColumn = parentColumn.table.constraints.primaryKey!!.columns.single()
    val toNode = createNode(keyColumn, bindingType)
    references(toNode, property, parentColumn)
}

fun <T : Any, TInstance, TReference> GBinding<T, *>.references(toNode: GNode<TransactionProcess, ResultRow, TReference>,
                                                               property: KProperty1<T, TInstance>,
                                                               parentColumn: ReferenceColumn<TReference>) {
    val reference = object : GReference<TransactionProcess> {
        override val from: GNode<TransactionProcess, ResultRow, *> = node
        override val to: GNode<TransactionProcess, ResultRow, TReference> = toNode

        override fun resolveStubs(process: TransactionProcess, fromStubs: List<GNodeStub<TransactionProcess, *, *>>) {
            val ids = fromStubs.map { it.id }.toSet()
            if (ids.isNotEmpty()) {
                val rows = query(parentColumn.table)
                        .where { parentColumn within ids }
                        .executeOn(process.transaction)
                        .toSet()

                val toStubs = to.fetchStubs(process, rows)
                fromStubs.forEach { stub ->
                    val parentId = stub.id
                    val references = toStubs.filter {
                        it.data!!.columnValue(parentColumn) == parentId
                    }
                    stub.references!!.put(property.name, references)
                }
            } else {
                fromStubs.forEach { stub ->
                    stub.references!!.put(property.name, emptyList())
                }
            }
        }
    }
    node.references.put(property.name, reference)
}
