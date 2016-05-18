package org.jetbrains.squash.dialect

import org.jetbrains.squash.*
import org.jetbrains.squash.definition.*
import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.query.*
import org.jetbrains.squash.statements.*

open class BaseSQLDialect(val name: String) : SQLDialect {
    override val definition: DefinitionSQLDialect = BaseDefinitionSQLDialect(this)

    override fun nameSQL(name: Name): String = when (name) {
        is QualifiedIdentifier<*> -> "${nameSQL(name.parent)}.${nameSQL(name.identifier)}"
        is Identifier -> idSQL(name)
        else -> error("Name '$name' is not supported by $this")
    }

    override fun idSQL(name: Name): String {
        val id = name.id
        if (isSqlIdentifier(id))
            return "$id"
        return "\"$id\""
    }

    protected open fun isSqlIdentifier(id: String): Boolean {
        if (id in SQL92_2003.keywords) return false
        fun Char.isIdentifierStart(): Boolean = this in 'a'..'z' || this in 'A'..'Z' || this == '_'
        fun String.isIdentifier() = !isEmpty() && first().isIdentifierStart() && all { it.isIdentifierStart() || it in '0'..'9' }
        return id.isIdentifier()
    }

    override fun literalSQL(value: Any?): SQLStatement = SQLBuilder().apply { appendLiteralSQL(this, value) }.build()

    protected open fun appendLiteralSQL(builder: SQLBuilder, value: Any?): Unit = with(builder) {
        when (value) {
            null -> append("NULL")
            is String -> {
                append("?")
                appendArgument(StringColumnType(), value)
            }
            is Int -> {
                append("?")
                appendArgument(IntColumnType, value)
            }
            else -> append(value.toString())
        }
    }

    protected open fun <T> appendDeclarationExpression(builder: SQLBuilder, expression: Expression<T>): Unit = with(builder) {
        when (expression) {
            is AliasExpression<T> -> {
                appendExpression(this, expression.expression)
                append(" AS ${nameSQL(expression.name)}")
            }
            is AliasColumn<T> -> {
                appendExpression(this, expression.column)
                append(" AS ${nameSQL(expression.label)}")
            }
            else -> appendExpression(this, expression)
        }
    }

    protected open fun <T> appendExpression(builder: SQLBuilder, expression: Expression<T>): Unit = with(builder) {
        when (expression) {
            is LiteralExpression -> appendLiteralSQL(this, expression.literal)
            is AliasColumn<T> -> {
                append(nameSQL(expression.label))
            }
            is NamedExpression<*, T> -> append(nameSQL(expression.name))
            is BinaryExpression<*, *, *> -> {
                appendExpression(this, expression.left)
                append(" ")
                appendBinaryOperator(this, expression)
                append(" ")
                appendExpression(this, expression.right)
            }
            is NotExpression -> {
                append("NOT ")
                appendExpression(this, expression.operand)
            }
            is SubQueryExpression<*> -> {
                append("(")
                appendSelectSQL(this, expression.query)
                append(")")
            }
            else -> error("Expression '$expression' is not supported by $this")
        }
    }

    protected open fun appendBinaryOperator(builder: SQLBuilder, expression: BinaryExpression<*, *, *>) = with(builder) {
        append(when (expression) {
            is EqExpression<*> -> "="
            is NotEqExpression<*> -> "<>"
            is LessExpression<*> -> "<"
            is GreaterExpression<*> -> ">"
            is LessEqExpression<*> -> "<="
            is GreaterEqExpression<*> -> ">="
            is AndExpression -> "AND"
            is OrExpression -> "OR"
            is PlusExpression -> "+"
            is MinusExpression -> "-"
            is MultiplyExpression -> "*"
            is DivideExpression -> "/"
            is LikeExpression -> "LIKE"
            else -> error("Expression '$expression' is not supported by $this")
        })
    }

    protected open fun appendSelectSQL(builder: SQLBuilder, query: Query): Unit = with(builder) {
        append("SELECT ")
        if (query.selection.isEmpty())
            append("*")
        else
            query.selection.forEachIndexed { index, expression ->
                if (index != 0) append(", ")
                appendDeclarationExpression(this, expression)
            }
        appendQuerySQL(builder, query)
    }

    protected open fun appendQuerySQL(builder: SQLBuilder, query: Query): Unit = with(builder) {
        if (query.schema.isNotEmpty()) {
            val tables = query.schema.filterIsInstance<QuerySchema.From>()
            append(" FROM ")
            tables.joinTo(this) { tableDeclarationName(it.table) }

            val innerJoins = query.schema.filter { it !is QuerySchema.From }
            if (innerJoins.any()) {
                innerJoins.forEach { join ->
                    when (join) {
                        is QuerySchema.InnerJoin -> {
                            append(" INNER JOIN ")
                            append(tableDeclarationName(join.table))
                            append(" ON ")
                            appendExpression(this, join.condition)
                        }
                        is QuerySchema.LeftOuterJoin -> {
                            append(" LEFT OUTER JOIN ")
                            append(tableDeclarationName(join.table))
                            append(" ON ")
                            appendExpression(this, join.condition)
                        }
                        is QuerySchema.RightOuterJoin -> {
                            append(" RIGHT OUTER JOIN ")
                            append(tableDeclarationName(join.table))
                            append(" ON ")
                            appendExpression(this, join.condition)
                        }
                    }
                }
            }
        }

        if (query.filter.isNotEmpty()) {
            append(" WHERE ")
            query.filter.forEachIndexed { index, expression ->
                if (index != 0) append(" AND ")
                appendExpression(this, expression)
            }
        }

        if (query.order.isNotEmpty()) {
            append(" ORDER BY ")
            query.order.forEachIndexed { index, order ->
                if (index != 0) append(", ")
                appendExpression(this, order.expression)
                when (order) {
                    is QueryOrder.Ascending -> { /* ASC is default */
                    }
                    is QueryOrder.Descending -> append(" DESC")
                }
            }
        }
    }

    private fun tableName(table: Table): String = when (table) {
        is AliasTable<*> -> idSQL(table.name)
        is Table -> nameSQL(table.tableName)
        else -> error("FieldCollection '$table' is not supported by $this")
    }

    private fun tableDeclarationName(table: Table): String = when (table) {
        is AliasTable<*> -> nameSQL(table.tableName) + " AS " + idSQL(table.name)
        is Table -> nameSQL(table.tableName)
        else -> error("FieldCollection '$table' is not supported by $this")
    }


    override fun <T> statementSQL(statement: Statement<T>): SQLStatement = when (statement) {
        is QueryStatement -> queryStatementSQL(statement)
        is InsertValuesStatement<*, *> -> insertValuesStatementSQL(statement)
        is InsertQueryStatement<*> -> insertQueryStatementSQL(statement)
        is UpdateQueryStatement<*> -> updateQueryStatementSQL(statement)
        is DeleteQueryStatement<*> -> deleteQueryStatementSQL(statement)
        else -> error("Statement '$statement' is not supported by $this")
    }

    protected open fun queryStatementSQL(query: QueryStatement): SQLStatement {
        return SQLBuilder().apply { appendSelectSQL(this, query) }.build()
    }

    protected open fun updateQueryStatementSQL(statement: UpdateQueryStatement<*>): SQLStatement = SQLBuilder().apply {
        append("UPDATE ")
        append(nameSQL(statement.table.tableName))
        append(" SET ")
        val values = statement.values.toList() // fix order
        values.forEachIndexed { index, value ->
            if (index > 0)
                append(", ")
            append(idSQL(value.first.name))
            append(" = ")
            appendExpression(this, value.second)
        }
        append(" ")
        appendQuerySQL(this, statement)
    }.build()

    protected open fun deleteQueryStatementSQL(statement: DeleteQueryStatement<*>): SQLStatement = SQLBuilder().apply {
        append("DELETE FROM ")
        append(nameSQL(statement.table.tableName))
        append(" ")
        appendQuerySQL(this, statement)
    }.build()

    protected open fun insertQueryStatementSQL(statement: InsertQueryStatement<*>): SQLStatement = SQLBuilder().apply {
        append("INSERT INTO ")
        append(nameSQL(statement.table.tableName))
        append(" ")
        appendSelectSQL(this, statement)
    }.build()

    protected open fun insertValuesStatementSQL(statement: InsertValuesStatement<*, *>): SQLStatement = SQLBuilder().apply {
        append("INSERT INTO ")
        append(nameSQL(statement.table.tableName))
        append(" (")
        val values = statement.values.toList() // fix order
        values.forEachIndexed { index, value ->
            if (index > 0)
                append(", ")
            append(idSQL(value.first.name))
        }
        append(") VALUES (")
        values.forEachIndexed { index, value ->
            if (index > 0)
                append(", ")
            appendLiteralSQL(this, value.second)
        }
        append(")")
    }.build()

    enum class ColumnProperty {
        NULLABLE, AUTOINCREMENT, DEFAULT
    }


    override fun toString(): String = "SQLDialect '$name'"
}
