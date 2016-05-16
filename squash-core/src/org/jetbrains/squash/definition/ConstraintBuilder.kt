package org.jetbrains.squash.definition

import org.jetbrains.squash.*

fun <V> Column<V>.primaryKey(pkName: String? = null): Column<V> = apply {
    table.addConstraint(PrimaryKeyConstraint(Identifier(pkName ?: "PK_${table.tableName.id}"), table, listOf(this)))
}

fun <V> Column<V>.uniqueIndex() = index(unique = true)
fun <V> Column<V>.index(name: String? = null, unique: Boolean = false): Column<V> = apply {
    val indexName = name ?: "IX_${this.name.referenceName()}"
    table.addConstraint(IndexConstraint(Identifier(indexName), table, unique, listOf(this)))
}

fun Table.index(vararg columns: Column<*>, name: String? = null, unique: Boolean = false) {
    val indexName = name ?: "IX_${tableName.id}_${columns.joinToString("_") { it.name.id }}"
    addConstraint(IndexConstraint(Identifier(indexName), this, unique, columns.toList()))
}

