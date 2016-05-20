@file:JvmName("Constraints")

package org.jetbrains.squash.definition

import org.jetbrains.squash.*

fun <V> Column<V>.primaryKey(pkName: String? = null): Column<V> = apply {
    table.constraints.add(PrimaryKeyConstraint(Identifier(pkName ?: "PK_${table.tableName.id}"), listOf(this)))
}

fun <V> Column<V>.uniqueIndex() = index(unique = true)
fun <V> Column<V>.index(name: String? = null, unique: Boolean = false): Column<V> = apply {
    val indexName = name ?: "IX_${this.name.referenceName()}"
    table.constraints.add(IndexConstraint(Identifier(indexName), listOf(this), unique))
}

// TODO: support full index column syntax, use builder like: index(name, unique).column(c1, ASC, NULLS_FIRST).column(c2, ...)
// Do not allow empty index. Warn on NULLS order on NOT_NULL columns.
//
// H2: index_col_name: col_name [ASC | DESC] [NULLS (FIRST | LAST)]
// MySQL: index_col_name: col_name [(length)] [ASC | DESC] ; # no NULLS order!
// PgSQL: ( { column | ( expression ) } [ COLLATE collation ] [ opclass ] [ ASC | DESC ] [ NULLS { FIRST | LAST } ] [, ...] )
fun Table.index(vararg columns: Column<*>, name: String? = null, unique: Boolean = false) {
    val indexName = name ?: "IX_${tableName.id}_${columns.joinToString("_") { it.name.id }}"
    constraints.add(IndexConstraint(Identifier(indexName), columns.toList(), unique))
}

