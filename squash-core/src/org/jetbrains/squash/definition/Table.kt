package org.jetbrains.squash.definition

import org.jetbrains.squash.query.*

/**
 * Represents a Table in a database with the given [tableName] and [tableColumns]
 */
interface Table : CompoundElement {
    val tableName: Identifier
    val tableColumns: List<Column<*>>
    val constraints: TableConstraints

    fun <T1, T2, TColumn : Column<T2>> replaceColumn(original: Column<T1>, replacement: TColumn): TColumn
}

