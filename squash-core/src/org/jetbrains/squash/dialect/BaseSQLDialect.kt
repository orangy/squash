package org.jetbrains.squash.dialect

import org.jetbrains.squash.*
import org.jetbrains.squash.definition.*
import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.statements.*

open class BaseSQLDialect(val name: String) : SQLDialect {
    override val definition: DefinitionSQLDialect = BaseDefinitionSQLDialect(this)

    fun <T> declarationExpressionSQL(expression: Expression<T>): String = when (expression) {
        is AliasExpression<T> -> expressionSQL(expression.expression) + " AS " + nameSQL(expression.name)
        else -> expressionSQL(expression)
    }

    override fun <T> expressionSQL(expression: Expression<T>): String {
        val sql = when (expression) {
            is LiteralExpression -> literalSQL(expression.literal)
            is NamedExpression<*, T> -> nameSQL(expression.name)
            is BinaryExpression<*, *, *> -> "${expressionSQL(expression.left)} ${binaryExpressionSQL(expression)} ${expressionSQL(expression.right)}"
            is NotExpression -> "NOT ${expressionSQL(expression.operand)}"
            is SubQueryExpression<*> -> "(${querySQL(expression.query).sql})"
            else -> error("Expression '$expression' is not supported by $this")
        }
        return sql
    }

    private fun binaryExpressionSQL(expression: BinaryExpression<*, *, *>): String = when (expression) {
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
        else -> error("Expression '$expression' is not supported by $this")
    }

    override fun nameSQL(name: Name): String = when (name) {
        is QualifiedIdentifier<*> -> "${nameSQL(name.parent)}.${nameSQL(name.identifier)}"
        is Identifier -> name.id
        else -> error("Name '$name' is not supported by $this")
    }

    override fun querySQL(query: Query): StatementSQL {
        val sql = buildString {
            append("SELECT ")
            if (query.selection.isEmpty())
                append("*")
            else
                query.selection.joinTo(this) { declarationExpressionSQL(it) }

            if (query.schema.isNotEmpty()) {
                val tables = query.schema.filterIsInstance<QuerySchema.From>()
                append(" FROM ")
                tables.joinTo(this) { fieldCollectionSQL(it.target) }

                val innerJoins = query.schema.filterIsInstance<QuerySchema.InnerJoin>()
                if (innerJoins.any()) {
                    innerJoins.forEach { join ->
                        append(" INNER JOIN ")
                        append(fieldCollectionSQL(join.target))
                        append(" ON ")
                        append(expressionSQL(join.condition))
                    }
                }
            }

            if (query.filter.isNotEmpty()) {
                append(" WHERE ")
                query.filter.joinTo(this, separator = " AND ") { expressionSQL(it) }
            }

        }
        return StatementSQL(sql, emptyMap())
    }

    private fun fieldCollectionSQL(target: ColumnOwner): String = when (target) {
        is Table -> nameSQL(target.tableName)
        else -> error("FieldCollection '$target' is not supported by $this")
    }


    override fun <T> statementSQL(statement: Statement<T>): StatementSQL = when (statement) {
        is InsertValuesStatement<*, *> -> insertValuesStatementSQL(statement)
        is InsertQueryStatement<*> -> insertQueryStatementSQL(statement)
        else -> error("Statement '$statement' is not supported by $this")
    }

    private fun insertQueryStatementSQL(statement: InsertQueryStatement<*>): StatementSQL {
        val querySQL = querySQL(statement)
        val sql = "INSERT INTO ${nameSQL(statement.table.tableName)} ${querySQL.sql}"
        return StatementSQL(sql, querySQL.indexes)
    }

    private fun insertValuesStatementSQL(statement: InsertValuesStatement<*, *>): StatementSQL {
        val arguments = mutableMapOf<Column<*>, Int>()
        val names = mutableListOf<Name>()
        val values = mutableListOf<Any?>()
        var index = 0
        for ((column, value) in statement.values) {
            names.add(column.name)
            values.add("?")
            arguments[column] = index++
        }
        val sql = "INSERT INTO ${nameSQL(statement.table.tableName)} (${names.map { it.id }.joinToString()}) VALUES (${values.joinToString()})"
        return StatementSQL(sql, arguments)
    }

    enum class ColumnProperty {
        NULLABLE, AUTOINCREMENT, DEFAULT
    }

    override fun literalSQL(value: Any?): String = when (value) {
        null -> "NULL"
        is String -> "'$value'"
        else -> value.toString()
    }


    override fun toString(): String = "SQLDialect '$name'"
}