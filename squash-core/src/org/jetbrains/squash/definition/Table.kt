package org.jetbrains.squash.definition

import org.jetbrains.squash.*

open class Table(name: String? = null) : ColumnOwnerImpl() {
    open val tableName = Identifier(name ?: javaClass.simpleName.removeSuffix("Table"))
    override fun toString(): String = "$tableName"

    override fun columnName(name: String): Name = QualifiedIdentifier(tableName, Identifier(name))
}

