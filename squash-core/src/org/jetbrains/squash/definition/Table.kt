package org.jetbrains.squash.definition

import org.jetbrains.squash.query.*

/**
 * Represents a Table in a database with the given [tableName] and [tableColumns]
 */
interface Table : NamedCompoundElement {
    val compoundColumns: List<Column<*>>
    val constraints: TableConstraints
}

