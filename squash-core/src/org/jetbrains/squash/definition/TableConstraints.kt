package org.jetbrains.squash.definition

interface TableConstraints {
    val elements: List<TableConstraint>
    fun add(constraint: TableConstraint)
}

interface TableConstraint {
    val name: Name
}

class PrimaryKeyConstraint(override val name: Name, val columns: List<Column<*>>) : TableConstraint {
    override fun toString(): String = "[PK] $columns"
}

class IndexConstraint(override val name: Name, val columns: List<Column<*>>, val unique: Boolean) : TableConstraint {
    override fun toString(): String = if (unique) "[UIX] $columns" else "[IX] $columns"
}

