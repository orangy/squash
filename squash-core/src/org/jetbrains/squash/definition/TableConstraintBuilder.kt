@file:JvmName("Constraints")

package org.jetbrains.squash.definition

fun <V> ColumnDefinition<V>.uniqueIndex() = index(unique = true)
fun <V> ColumnDefinition<V>.index(name: String? = null, unique: Boolean = false) = apply {
    val indexName = name ?: "IX_${this.name.referenceName()}"
    compound.constraints.add(IndexConstraint(Identifier(indexName), listOf(this), unique))
}

// TODO: support full index column syntax, use builder like: index(name, unique).column(c1, ASC, NULLS_FIRST).column(c2, ...)
// Do not allow empty index. Warn on NULLS order on NOT_NULL columns.
//
// H2: index_col_name: col_name [ASC | DESC] [NULLS (FIRST | LAST)]
// MySQL: index_col_name: col_name [(length)] [ASC | DESC] ; # no NULLS order!
// PgSQL: ( { column | ( expression ) } [ COLLATE collation ] [ opclass ] [ ASC | DESC ] [ NULLS { FIRST | LAST } ] [, ...] )
fun Table.index(vararg columns: ColumnDefinition<*>, name: String? = null, unique: Boolean = false) {
    val indexName = name ?: "IX_${compoundName.id}_${columns.joinToString("_") { it.name.id }}"
    constraints.add(IndexConstraint(Identifier(indexName), columns.toList(), unique))
}

fun <V> ColumnDefinition<V>.primaryKey(pkName: String? = null) = apply {
    val indexName = "PK_${compound.compoundName.id}"
    check(compound.constraints.primaryKey == null) {
        "Cannot set primary key to $indexName because it was already created for table `$this`"
    }
    compound.constraints.primaryKey = PrimaryKeyConstraint(Identifier(pkName ?: indexName), listOf(this))
}

fun Table.primaryKey(vararg columns: ColumnDefinition<*>, name: String? = null) {
    val indexName = name ?: "PK_${compoundName.id}_${columns.joinToString("_") { it.name.id }}"
    check(constraints.primaryKey == null) {
        "Cannot set primary key to $indexName because it was already created for table `$this`"
    }
    constraints.primaryKey = PrimaryKeyConstraint(Identifier(indexName), columns.toList())
}

