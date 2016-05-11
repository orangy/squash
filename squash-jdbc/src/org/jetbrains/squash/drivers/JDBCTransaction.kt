package org.jetbrains.squash.drivers

import org.jetbrains.squash.*
import org.jetbrains.squash.definition.*
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

    override fun <T> executeStatement(statement: Statement<T>): T {
        val statementSQL = connection.dialect.statementSQL(statement)
        val preparedStatement = jdbcConnection.prepareStatement(statementSQL.sql)
        statement.forEachArgument { column, value ->
            val index = statementSQL.indexes[column] ?: error("${connection.dialect} didn't provide index for column '$column'")
            preparedStatement.prepareValue(index, column.type, value)
        }
        preparedStatement.execute()
        return preparedStatement.resultFor(statement)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> PreparedStatement.resultFor(statement: Statement<T>): T {
        when (statement) {
            is InsertValuesStatement<*, *> -> {
                val response = JDBCResponse(generatedKeys)
                val rows = response.rows
                if (rows.empty)
                    return Unit as T
                return rows.single()[response.tableColumns.single()] as T
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