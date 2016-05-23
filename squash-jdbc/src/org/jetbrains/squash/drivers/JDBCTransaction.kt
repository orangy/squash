package org.jetbrains.squash.drivers

import org.jetbrains.squash.connection.*
import org.jetbrains.squash.definition.*
import org.jetbrains.squash.dialect.*
import org.jetbrains.squash.results.*
import org.jetbrains.squash.schema.*
import org.jetbrains.squash.statements.*
import org.jetbrains.squash.statements.Statement
import java.sql.*

open class JDBCTransaction(override val connection: DatabaseConnection, val connector: () -> Connection) : Transaction {
    var _jdbcConnection: Connection? = null

    val jdbcConnection: Connection get() = _jdbcConnection ?: run {
        _jdbcConnection = connector()
        _jdbcConnection!!
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> PreparedStatement.resultFor(statement: Statement<T>): T {
        when (statement) {
            is InsertValuesStatement<*, *> -> {
                val response = JDBCResponse(this@JDBCTransaction, generatedKeys)
                val rows = response.rows
                if (rows.empty)
                    return Unit as T
                val keyColumn = response.columns.single()
                return rows.single().get<T>(keyColumn)
            }
            is InsertQueryStatement<*> -> {
                val response = JDBCResponse(this@JDBCTransaction, generatedKeys)
                val rows = response.rows
                if (rows.empty)
                    return emptySequence<Nothing>() as T
                val keyColumn = response.columns.single()
                return rows.single().get<T>(keyColumn)
            }
            is QueryStatement -> {
                val response = JDBCResponse(this@JDBCTransaction, resultSet)
                return response as T
            }

            is DeleteQueryStatement<*>,
            is UpdateQueryStatement<*> -> {
                return Unit as T
            }
            else -> error("Cannot extract result for $statement")
        }
    }

    override fun <T> executeStatement(statement: Statement<T>): T {
        val statementSQL = connection.dialect.statementSQL(statement)
        val returnColumn: Column<*>? = if (statement is InsertValuesStatement<*, *>) statement.generatedKeyColumn else null
        val preparedStatement = jdbcConnection.prepareStatement(statementSQL, returnColumn)
        preparedStatement.execute()
        return preparedStatement.resultFor(statement)
    }

    override fun executeStatement(statement: SQLStatement): Response {
        try {

            val preparedStatement = jdbcConnection.prepareStatement(statement)
            val executionResult = preparedStatement.execute()
            return when (executionResult) {
                true -> JDBCResponse(this, preparedStatement.resultSet)
                false -> Response.Empty
            }
        } catch (ex: SQLException) {
            if (DriverManager.getLogWriter() != null) {
                DriverManager.println("SQLStatement: " + statement.toString())
                throw ex
            } else {
                throw JDBCException(ex, statement)
            }
        }
    }

    fun Connection.prepareStatement(statementSQL: SQLStatement, returnColumn: Column<*>? = null): PreparedStatement {
        val preparedStatement = if (returnColumn == null) prepareStatement(statementSQL.sql) else
            prepareStatement(statementSQL.sql, arrayOf(connection.dialect.idSQL(returnColumn.name)))

        statementSQL.arguments.forEach { arg ->
            preparedStatement.setObject(arg.index + 1, arg.value)
        }
        return preparedStatement
    }

    override fun executeStatement(sql: String): Response = executeStatement(SQLStatement(sql, emptyList()))

    override fun commit() {
        _jdbcConnection?.commit()
    }

    override fun rollback() {
        _jdbcConnection?.rollback()
    }

    override fun databaseSchema(): DatabaseSchema = JDBCDatabaseSchema(connection.dialect, this)

    override fun close() {
        _jdbcConnection?.close()
    }
}