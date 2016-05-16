package org.jetbrains.squash.definition

import org.jetbrains.squash.*

interface ConstraintOwner {
    val constraints: List<Constraint>
}

interface Constraint {
    val name: Name
    val owner: ConstraintOwner
}

class PrimaryKeyConstraint(override val name: Name, override val owner: ConstraintOwner, val columns: List<Column<*>>) : Constraint {
    override fun toString(): String = "[PK] $columns"
}

class IndexConstraint(override val name: Name, override val owner: ConstraintOwner, val unique: Boolean, val columns: List<Column<*>>) : Constraint {
    override fun toString(): String = if (unique) "[UIX] $columns" else "[IX] $columns"
}

