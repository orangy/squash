package org.jetbrains.squash.graph

import org.jetbrains.squash.definition.*
import kotlin.reflect.*
import kotlin.reflect.jvm.*

fun <T : Any, TProperty, TReference> TransactionBindingsNode<T, *>.reference(property: KProperty1<T, TProperty>,
                                                                          referenceColumn: ReferenceColumn<TReference>) {
    val targetColumn = referenceColumn.reference
    val propertyType = (property.returnType.javaType as Class<*>).kotlin
//    val propertyType = property.getBindingType()
    val toNode = bindings.bind(propertyType, targetColumn)
    val reference = TransactionReferenceLink(this, toNode, referenceColumn, property.name)
    references.put(property.name, reference)
}

fun <T : Any, TProperty, TReference> TransactionBindingsNode<T, *>.references(property: KProperty1<T, TProperty>,
                                                                           fromColumn: ReferenceColumn<TReference>,
                                                                           toColumn: ReferenceColumn<TReference>) {
    val targetColumn = toColumn.reference
    val propertyType = property.getBindingType()
    val toNode = bindings.bind(propertyType, targetColumn)
    val reference = TransactionReferenceMM(this, toNode, fromColumn, toColumn, property.name)
    references.put(property.name, reference)
}

fun <T : Any, TProperty, TReference> TransactionBindingsNode<T, *>.references(property: KProperty1<T, TProperty>,
                                                                           parentColumn: ReferenceColumn<TReference>) {
    val targetColumn = parentColumn.table.constraints.primaryKey!!.columns.single()
    val propertyType = property.getBindingType()
    val toNode = bindings.bind(propertyType, targetColumn)
    val reference = TransactionReference1M(this, toNode, parentColumn, property.name)
    references.put(property.name, reference)
}


