package org.jetbrains.squash.definition

open class TableDefinition(name: String? = null) : Table {
    override val tableName = Identifier(name ?: javaClass.simpleName.removeSuffix("Table"))

    override fun toString(): String = "$tableName"

    override val constraints: TableConstraints = object : TableConstraints {
        override val elements = mutableListOf<TableConstraint>()
        override fun add(constraint: TableConstraint) {
            elements.add(constraint)
        }
    }

    private val _tableColumns = mutableListOf<Column<*>>()
    override val tableColumns: List<Column<*>> get() = _tableColumns

    fun <T> addColumn(column: Column<T>): Column<T> {
        require(column.table == this) { "Can't add column '$column' from different table" }
        require(tableColumns.none { it.name.id.equals(column.name.id, ignoreCase = true) }) { "Column with the same identifier '$column' already exists" }
        _tableColumns.add(column)
        return column
    }

    fun <T, C : ColumnType> createColumn(name: String, type: C): Column<T> {
        return addColumn(DataColumn<T>(this, columnName(name), type))
    }

    override fun <T1, T2> replaceColumn(original: Column<T1>, replacement: Column<T2>): Column<T2> {
        require(original.table == this) { "Can't replace column '$original' from different table" }
        require(replacement.table == this) { "Can't replace with column '$replacement' from different table" }

        val index = _tableColumns.indexOf(original)
        if (index < 0) error("Original column `$original` not found in this table `$this`")
        _tableColumns[index] = replacement
        return replacement
    }

    private fun columnName(name: String): Name = QualifiedIdentifier(tableName, Identifier(name))
}