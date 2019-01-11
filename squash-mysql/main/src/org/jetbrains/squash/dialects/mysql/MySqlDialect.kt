package org.jetbrains.squash.dialects.mysql

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.dialect.*
import org.jetbrains.squash.query.*

object MySqlDialect : BaseSQLDialect("MySQL") {
    override fun idSQL(name: Name): String {
        val id = name.id
        return if (isSqlIdentifier(id)) id else "`$id`"
    }

    private val mysqlKeywords = setOf("LONG", "LOAD")
    override fun isSqlIdentifier(id: String): Boolean {
        if (id.toUpperCase() in mysqlKeywords) return false
        return super.isSqlIdentifier(id)
    }

    override fun appendOrderExpression(builder: SQLStatementBuilder, order: QueryOrder) {
        // NULLS LAST
        builder.append("ISNULL(")
        appendExpression(builder, order.expression)
        builder.append("), ")

        // Main order
        appendExpression(builder, order.expression)
        when (order) {
            is QueryOrder.Ascending -> { /* ASC is default */
            }
            is QueryOrder.Descending -> builder.append(" DESC")
        }
    }

    override val definition: DefinitionSQLDialect = object : BaseDefinitionSQLDialect(this) {
        override fun indicesSQL(table: TableDefinition): List<SQLStatement> =
            table.constraints.elements.filterIsInstance<IndexConstraint>().map {
                SQLStatementBuilder().apply {
                    val unique = if (it.unique) " UNIQUE" else ""
                    val indexName = dialect.idSQL(it.name)
                    val tableName = dialect.idSQL(table.compoundName)

                    append("CREATE$unique INDEX $indexName ON $tableName (")
                    it.columns.forEachIndexed { index, column ->
                        if (index > 0)
                            append(", ")
                        append(dialect.idSQL(column.name))
                    }
                    append(")")
                }.build()
            }

        override fun columnTypeSQL(builder: SQLStatementBuilder, type: ColumnType): Unit {
            when (type) {
                is UUIDColumnType -> builder.append("BINARY(16)")
                else -> super.columnTypeSQL(builder, type)
            }
        }
    }
}
