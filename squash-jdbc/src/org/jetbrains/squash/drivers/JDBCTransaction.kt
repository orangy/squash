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
                val keyColumn = statement.generatedKeyColumn
                if (keyColumn == null) {
                    return Unit as T
                } else {
                    val response = JDBCResponse(connection.conversion, jdbcStatement.generatedKeys)
                    val rows = response.rows
                    if (rows.empty)
                        return Unit as T
                    val generatedColumn = response.columns.single()
                    val columnValue = rows.single().columnValue(keyColumn.type.runtimeType, generatedColumn.columnIndex - 1)
                    return columnValue as T
                }
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
        connection.monitor.beforeStatement(this, statementSQL)
        val preparedStatement = jdbcConnection.prepareStatement(statementSQL, returnColumn)
        preparedStatement.execute()
        val result = resultFor(preparedStatement, statement)
        connection.monitor.afterStatement(this, statementSQL, result)
        return result
    }

    override fun executeStatement(statement: SQLStatement): Response {
        try {
            val preparedStatement = jdbcConnection.prepareStatement(statement)
            connection.monitor.beforeStatement(this, statement)
            val executionResult = preparedStatement.execute()
            val result = when (executionResult) {
                true -> JDBCResponse(connection.conversion, preparedStatement.resultSet)
                false -> Response.Empty
            }
            connection.monitor.afterStatement(this, statement, result)
            return result
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

    override fun createBlob(bytes: ByteArray): BinaryObject {
        return JDBCBinaryObject(bytes)
    }
}