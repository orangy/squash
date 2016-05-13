package org.jetbrains.squash.drivers

import org.jetbrains.squash.*
import org.jetbrains.squash.definition.*
import org.jetbrains.squash.dialect.*
import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.results.*
import org.jetbrains.squash.statements.*
import org.jetbrains.squash.statements.Statement
import java.sql.*

class JDBCTransaction(override val connection: DatabaseConnection, val connector: () -> Connection) : Transaction {
    var _jdbcConnection: Connection? = null

    val jdbcConnection: Connection get() = _jdbcConnection ?: run {
        _jdbcConnection = connector()
        _jdbcConnection!!
    }

    private fun PreparedStatement.prepareValue(index: Int, type: ColumnType, value: Any?): Unit {
        if (value == null)
            setObject(index + 1, null)
        else when (type) {
            is IntColumnType -> setInt(index + 1, value as Int)
            is StringColumnType -> setString(index + 1, value as String)
            is ReferenceColumnType<*> -> prepareValue(index, type.column.type, value)
            is NullableColumnType -> prepareValue(index, type.columnType, value)
            else -> setObject(index, value)
        }
    }

    override fun executeQuery(query: Query): Response {
        val statementSQL = connection.dialect.querySQL(query)
        val preparedStatement = jdbcConnection.prepareStatement(statementSQL.sql)
        statementSQL.arguments.forEach { arg ->
            preparedStatement.prepareValue(arg.index, arg.columnType, arg.value)
        }
        preparedStatement.executeQuery()
        val response = JDBCResponse(preparedStatement.resultSet)
        return response
    }

    override fun <T> executeStatement(statement: Statement<T>): T {
        val statementSQL = connection.dialect.statementSQL(statement)
        val preparedStatement = jdbcConnection.prepareStatement(statementSQL.sql, java.sql.Statement.RETURN_GENERATED_KEYS)
        statementSQL.arguments.forEach { arg ->
            preparedStatement.prepareValue(arg.index, arg.columnType, arg.value)
        }
        preparedStatement.executeUpdate()
        return preparedStatement.resultFor(statement)
    }

    override fun executeStatement(statementSQL: SQLStatement): Unit {
        val preparedStatement = jdbcConnection.prepareStatement(statementSQL.sql, java.sql.Statement.RETURN_GENERATED_KEYS)
        statementSQL.arguments.forEach { arg ->
            preparedStatement.prepareValue(arg.index, arg.columnType, arg.value)
        }
        preparedStatement.execute()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> PreparedStatement.resultFor(statement: Statement<T>): T {
        when (statement) {
            is InsertValuesStatement<*, *> -> {
                val response = JDBCResponse(generatedKeys)
                val rows = response.rows
                if (rows.empty)
                    return Unit as T
                return rows.single().get<T>(response.columns.single())
            }
            is InsertQueryStatement<*> -> {
                val response = JDBCResponse(generatedKeys)
                val rows = response.rows
                if (rows.empty)
                    return emptySequence<Nothing>() as T
                return rows.single().get<T>(response.columns.single())
            }
            else -> error("Cannot extract result for $statement")
        }
    }

    override fun executeStatement(sql: String) {
        jdbcConnection.prepareStatement(sql).executeUpdate()
    }

    override fun commit() {
        _jdbcConnection?.commit()
    }

    override fun querySchema(): DatabaseSchema = JDBCDatabaseSchema(jdbcConnection)

    override fun close() {
        _jdbcConnection?.close()
    }
}