package org.jetbrains.squash.definition

/**
 * Represents a collection of constraints in a database
 */
interface TableConstraints {
    val elements: List<TableConstraint>
    fun add(constraint: TableConstraint)
}

/**
 * Represents a constraint in a database
 */
interface TableConstraint {
    val name: Name
}

class PrimaryKeyConstraint(override val name: Name, val columns: List<Column<*>>) : TableConstraint {
    override fun toString(): String = "[PK] $columns"
}

class IndexConstraint(override val name: Name, val columns: List<Column<*>>, val unique: Boolean) : TableConstraint {
    override fun toString(): String = if (unique) "[UIX] $columns" else "[IX] $columns"
}

