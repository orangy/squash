package org.jetbrains.squash.dialect

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.query.*
import org.jetbrains.squash.statements.*

open class BaseSQLDialect(val name: String) : SQLDialect {
    override val definition: DefinitionSQLDialect = BaseDefinitionSQLDialect(this)

    override fun nameSQL(name: Name): String = when (name) {
        is QualifiedIdentifier<*> -> "${nameSQL(name.parent)}.${nameSQL(name.identifier)}"
        is Identifier -> idSQL(name)
        else -> error("Name '$name' is not supported by ${this@BaseSQLDialect}")
    }

    override fun idSQL(name: Name): String {
        val id = name.id
        return if (isSqlIdentifier(id)) id else "\"$id\""
    }

    @Suppress("NOTHING_TO_INLINE")
    inline private fun Char.isIdentifierStart(): Boolean = this in 'a'..'z' || this in 'A'..'Z' || this == '_'

    open fun isSqlIdentifier(id: String): Boolean {
        if (id.isEmpty()) return false
        if (id.toUpperCase() in SQL92_2003.keywords) return false
        return id[0].isIdentifierStart() && id.all { it.isIdentifierStart() || it in '0'..'9' }
    }

    override fun appendLiteralSQL(builder: SQLStatementBuilder, value: Any?) {
        if (value != null)
            builder.append("?")
        when (value) {
            null -> builder.append("NULL")
            else -> builder.appendArgument(value)
        }
    }

    open fun <T> appendDeclarationExpression(builder: SQLStatementBuilder, expression: Expression<T>): Unit = with(builder) {
        when (expression) {
            is AllTableColumnsExpression -> {
                val element = expression.element
                val name = when (element) {
                    is Table -> nameSQL(element.compoundName)
                    is AliasCompoundElement -> nameSQL(element.label)
                    else -> error("Expression '$element' is not supported by ${this@BaseSQLDialect}")
                }
                append(name)
                append(".*")
            }
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

    override fun <T> appendExpression(builder: SQLStatementBuilder, expression: Expression<T>): Unit = with(builder) {
        when (expression) {
            is LiteralExpression -> appendLiteralSQL(this, expression.literal)
            is AliasColumn<T> -> {
                append(nameSQL(expression.label))
            }
            is NamedExpression<*, T> -> append(nameSQL(expression.name))
            is InExpression<*> -> {
                appendExpression(this, expression.value)
                append(" IN (")
                expression.values.forEachIndexed { index, value ->
                    if (index > 0)
                        append(", ")
                    appendLiteralSQL(this, value)
                }
                append(")")
            }
            is BinaryExpression<*, *, *> -> {
                appendBinaryExpression(this, expression)
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
            is FunctionExpression -> {
                appendFunctionExpression(this, expression)
            }
            is DialectExtension -> {
                expression.appendTo(this, this@BaseSQLDialect)
            }
            else -> error("Expression '$expression' is not supported by ${this@BaseSQLDialect}")
        }
    }

    open fun appendBinaryExpression(builder: SQLStatementBuilder, expression: BinaryExpression<*, *, *>) = with(builder) {
        if (expression.right is LiteralExpression && expression.right.literal == null) {
            when (expression) {
                is EqExpression<*> -> {
                    appendExpression(this, expression.left)
                    append(" IS NULL")
                }
                is NotEqExpression<*> -> {
                    appendExpression(this, expression.left)
                    append(" IS NOT NULL")
                }
                else -> error("NULL can only be used in equality operations, but an expression was ${expression.javaClass.simpleName}")
            }
        } else {
            appendExpression(this, expression.left)
            append(" ")
            appendBinaryOperator(this, expression)
            append(" ")
            appendExpression(this, expression.right)
        }
    }

    open fun appendBinaryOperator(builder: SQLStatementBuilder, expression: BinaryExpression<*, *, *>) = with(builder) {
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
            else -> error("Expression '$expression' is not supported by ${this@BaseSQLDialect}")
        })
    }

    open fun <T> appendFunctionExpression(builder: SQLStatementBuilder, expression: FunctionExpression<T>) = with(builder) {
        when (expression) {
            is CountExpression -> {
                append("COUNT(")
                appendExpression(this, expression.value)
                append(")")
            }
            is CountDistinctExpression -> {
                append("COUNT(DISTINCT ")
                appendExpression(this, expression.value)
                append(")")
            }
			is GeneralFunctionExpression -> {
				append("${expression.name}(")
				appendExpression(this, expression.value)
				append(")")
			}
            else -> error("Function '$expression' is not supported by ${this@BaseSQLDialect}")
        }
    }

    open fun appendSelectSQL(builder: SQLStatementBuilder, query: Query) {
        builder.append("SELECT ")
        if (query.selection.isEmpty()) {
            builder.append("*")
        } else {
            query.selection.forEachIndexed { index, expression ->
                if (index != 0) builder.append(", ")
                appendDeclarationExpression(builder, expression)
            }
        }
        appendQuerySQL(builder, query)
    }

    open fun appendOrderSQL(builder: SQLStatementBuilder, query: Query) {
        if (query.order.isEmpty())
            return

        builder.append(" ORDER BY ")
        query.order.forEachIndexed { index, order ->
            if (index != 0) builder.append(", ")
            appendOrderExpression(builder, order)
        }
    }

    open fun appendModifiersSQL(builder: SQLStatementBuilder, query: Query) {
        if (query.modifiers.isEmpty())
            return
        query.modifiers.forEach {
            appendModifierSQL(builder, it)
        }
    }

    open fun appendModifierSQL(builder: SQLStatementBuilder, modifier: QueryModifier) {
        when (modifier) {
            is QueryLimit -> {
                builder.append(" LIMIT ?")
                builder.appendArgument(modifier.limit)
                if (modifier.offset != 0L) {
                    builder.append(" OFFSET ?")
                    builder.appendArgument(modifier.offset)
                }
            }
            else -> error("Query modifier $modifier is not supported by ${this@BaseSQLDialect}")
        }
    }

    open fun appendGroupingSQL(builder: SQLStatementBuilder, query: Query) {
        if (query.grouping.isEmpty())
            return

        builder.append(" GROUP BY ")
        query.grouping.forEachIndexed { index, order ->
            if (index != 0) builder.append(", ")
            appendExpression(builder, order)
        }
    }

    open fun appendHavingSQL(builder: SQLStatementBuilder, query: Query) {
        if (query.having.isEmpty())
            return

        builder.append(" HAVING ")
        query.having.forEachIndexed { index, order ->
            if (index != 0) builder.append(" AND ")
            appendExpression(builder, order)
        }
    }

    open fun appendOrderExpression(builder: SQLStatementBuilder, order: QueryOrder) {
        appendExpression(builder, order.expression)
        when (order) {
            is QueryOrder.Ascending -> { /* ASC is default */
            }
            is QueryOrder.Descending -> builder.append(" DESC")
        }
        builder.append(" NULLS LAST")
    }

    open fun appendFilterSQL(builder: SQLStatementBuilder, query: Query) {
        if (query.filter.isEmpty())
            return

        builder.append(" WHERE ")
        query.filter.forEachIndexed { index, expression ->
            if (index != 0) builder.append(" AND ")
            appendExpression(builder, expression)
        }
    }

    open fun appendCompoundSQL(builder: SQLStatementBuilder, query: Query) {
        if (query.compound.isEmpty())
            return

        val tables = query.compound.filterIsInstance<QueryCompound.From>()
        builder.append(" FROM ")
        tables.forEach {
            appendCompoundElementSQL(builder, it.element)
        }

        val innerJoins = query.compound.filter { it !is QueryCompound.From }
        if (innerJoins.any()) {
            innerJoins.forEach { join ->
                val condition = when (join) {
                    is QueryCompound.InnerJoin -> {
                        builder.append(" INNER JOIN ")
                        join.condition
                    }
                    is QueryCompound.LeftOuterJoin -> {
                        builder.append(" LEFT OUTER JOIN ")
                        join.condition
                    }
                    is QueryCompound.RightOuterJoin -> {
                        builder.append(" RIGHT OUTER JOIN ")
                        join.condition
                    }
                    is QueryCompound.From -> error("From clauses should have been filtered out")
                }
                appendCompoundElementSQL(builder, join.element)
                builder.append(" ON ")
                appendExpression(builder, condition)
            }
        }
    }

    open fun appendQuerySQL(builder: SQLStatementBuilder, query: Query): Unit = with(builder) {
        appendCompoundSQL(builder, query)
        appendFilterSQL(builder, query)
        appendGroupingSQL(builder, query)
        appendHavingSQL(builder, query)
        appendOrderSQL(builder, query)
        appendModifiersSQL(builder, query)
    }

    override fun appendCompoundElementSQL(builder: SQLStatementBuilder, element: CompoundElement): Unit = with(builder) {
        when (element) {
            is Table -> builder.append(nameSQL(element.compoundName))
            is AliasCompoundElement -> {
                appendCompoundElementSQL(this, element.element)
                builder.append(" AS " + nameSQL(element.label))
            }
            is QueryStatement -> {
                append("(")
                appendSelectSQL(this, element)
                append(")")
            }
            else -> error("Compound '$element' is not supported by ${this@BaseSQLDialect}")
        }
    }

    override fun <T> statementSQL(statement: Statement<T>): SQLStatement = when (statement) {
        is QueryStatement -> queryStatementSQL(statement)
        is InsertValuesStatement<*, *> -> insertValuesStatementSQL(statement)
        is InsertQueryStatement<*> -> insertQueryStatementSQL(statement)
        is UpdateQueryStatement<*> -> updateQueryStatementSQL(statement)
        is DeleteQueryStatement<*> -> deleteQueryStatementSQL(statement)
        else -> error("Statement '$statement' is not supported by ${this@BaseSQLDialect}")
    }

    open fun queryStatementSQL(query: QueryStatement): SQLStatement {
        return SQLStatementBuilder().apply { appendSelectSQL(this, query) }.build()
    }

    open fun updateQueryStatementSQL(statement: UpdateQueryStatement<*>): SQLStatement = SQLStatementBuilder().apply {
        append("UPDATE ")
        append(nameSQL(statement.table.compoundName))
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

    open fun deleteQueryStatementSQL(statement: DeleteQueryStatement<*>): SQLStatement = SQLStatementBuilder().apply {
        append("DELETE FROM ")
        append(nameSQL(statement.table.compoundName))
        append(" ")
        appendQuerySQL(this, statement)
    }.build()

    open fun insertQueryStatementSQL(statement: InsertQueryStatement<*>): SQLStatement = SQLStatementBuilder().apply {
        append("INSERT INTO ")
        append(nameSQL(statement.table.compoundName))
        append(" ")
        appendSelectSQL(this, statement)
    }.build()

    open fun insertValuesStatementSQL(statement: InsertValuesStatement<*, *>): SQLStatement = SQLStatementBuilder().apply {
        append("INSERT INTO ")
        append(nameSQL(statement.table.compoundName))
        append(" (")
        val values = statement.values.entries.toList() // fix order
        values.forEachIndexed { index, value ->
            if (index > 0)
                append(", ")
            append(idSQL(value.key.name))
        }
        append(") VALUES (")
        values.forEachIndexed { index, value ->
            if (index > 0)
                append(", ")
            appendLiteralSQL(this, value.value)
        }
        append(")")
    }.build()

    override fun toString(): String = "SQLDialect '$name'"
}
