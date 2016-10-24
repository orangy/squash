package org.jetbrains.squash.graph

import org.jetbrains.squash.definition.*
import kotlin.reflect.*

inline fun <T : Any, reified TProperty, TReference> TransactionBindingsNode<*>.reference(property: KProperty1<T, TProperty>,
                                                                                                  referenceColumn: ReferenceColumn<TReference>) {
    val targetColumn = referenceColumn.reference
    val propertyType = TProperty::class
    val toNode = bindings.bind(propertyType, targetColumn)
    val reference = TransactionReferenceLink(this, toNode, referenceColumn, property.name)
    references.put(property.name, reference)
}

inline fun <T : Any, reified TProperty, TReference> TransactionBindingsNode<*>.references(property: KProperty1<T, List<TProperty>>,
                                                                              fromColumn: ReferenceColumn<TReference>,
                                                                              toColumn: ReferenceColumn<TReference>) {
    val targetColumn = toColumn.reference
    val propertyType = TProperty::class
    val toNode = bindings.bind(propertyType, targetColumn)
    val reference = TransactionReferenceMM(this, toNode, fromColumn, toColumn, property.name)
    references.put(property.name, reference)
}

inline fun <T : Any, reified TProperty, TReference> TransactionBindingsNode<*>.references(property: KProperty1<T, List<TProperty>>,
                                                                              parentColumn: ReferenceColumn<TReference>) {
    val targetColumn = parentColumn.table.constraints.primaryKey!!.columns.single()
    val propertyType = TProperty::class
    val toNode = bindings.bind(propertyType, targetColumn)
    val reference = TransactionReference1M(this, toNode, parentColumn, property.name)
    references.put(property.name, reference)
}


