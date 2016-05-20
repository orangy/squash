package org.jetbrains.squash.definition

interface Table {
    val tableName: Identifier
    val tableColumns: List<Column<*>>
    val constraints: TableConstraints

    fun <T1, T2> replaceColumn(original: Column<T1>, replacement: Column<T2>): Column<T2>

}

