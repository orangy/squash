package org.jetbrains.squash.definition

/**
 * Represents a collection of constraints in a database
 */
interface TableConstraints {
    var primaryKey : PrimaryKeyConstraint?
    val elements: List<TableConstraint>
    fun add(constraint: TableConstraint)
}

/**
 * Represents a constraint in a database
 */
interface TableConstraint {
    val name: Name
}

class PrimaryKeyConstraint(override val name: Name, val columns: List<ColumnDefinition<*>>) : TableConstraint {
    override fun toString(): String = "[PK] $columns"
}

class ForeignKeyConstraint(override val name: Name, val sources: List<ColumnDefinition<*>>, val destinations: List<ColumnDefinition<*>>) : TableConstraint {
    override fun toString(): String = "[FK] $sources -> $destinations"
}

class IndexConstraint(override val name: Name, val columns: List<ColumnDefinition<*>>, val unique: Boolean) : TableConstraint {
    override fun toString(): String = if (unique) "[UIX] $columns" else "[IX] $columns"
}

