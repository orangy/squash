package org.jetbrains.squash.drivers

import org.jetbrains.squash.connection.*
import org.jetbrains.squash.definition.*
import org.jetbrains.squash.dialect.*
import org.jetbrains.squash.results.*
import org.jetbrains.squash.schema.*
import org.jetbrains.squash.statements.*
import org.jetbrains.squash.statements.Statement
import java.sql.*

open class JDBCTransaction(override val connection: JDBCConnection) : Transaction {
    private var _jdbcConnection: Connection? = null

    val jdbcConnection: Connection get() = _jdbcConnection ?: run {
        _jdbcConnection = connection.connector()
        _jdbcConnection!!
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> resultFor(jdbcStatement: PreparedStatement, statement: Statement<T>): T {
        when (statement) {
            is InsertValuesStatement<*, *> -> {
                val response = JDBCResponse(connection.conversion, jdbcStatement.generatedKeys)
                val rows = response.rows
                if (rows.empty)
                    return Unit as T
                val keyColumn = response.columns.single()
                val columnValue = rows.single().get<Any>(keyColumn)
                return columnValue as T
            }
            is InsertQueryStatement<*> -> {
                // TODO: support generating sequence for fetch keys
                return emptySequence<Nothing>() as T
            }
            is QueryStatement -> {
                val response = JDBCResponse(connection.conversion, jdbcStatement.resultSet)
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
        return resultFor(preparedStatement, statement)
    }

    override fun executeStatement(statement: SQLStatement): Response {
        try {
            val preparedStatement = jdbcConnection.prepareStatement(statement)
            val executionResult = preparedStatement.execute()
            return when (executionResult) {
                true -> JDBCResponse(connection.conversion, preparedStatement.resultSet)
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
            preparedStatement.setObject(arg.index + 1, connection.conversion.convertValueToDatabase(arg.value))
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

    override fun databaseSchema(): DatabaseSchema = JDBCDatabaseSchema(this)

    override fun close() {
        _jdbcConnection?.close()
    }
}