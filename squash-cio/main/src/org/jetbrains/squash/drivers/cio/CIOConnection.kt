package org.jetbrains.squash.drivers.cio

import io.ktor.experimental.client.sql.*
import kotlinx.coroutines.channels.*
import org.jetbrains.squash.connection.*
import org.jetbrains.squash.definition.*
import org.jetbrains.squash.dialect.*
import org.jetbrains.squash.results.*
import org.jetbrains.squash.schema.*
import org.jetbrains.squash.statements.*
import org.jetbrains.squash.statements.Statement
import kotlin.reflect.*

open class CIOConnection(private val client: SqlClient, override val dialect: SQLDialect) : DatabaseConnection {
    override val monitor = DefaultDatabaseConnectionMonitor()

    override suspend fun createTransaction() = CIOTransaction(this, client.connection())
    override fun close() {
        client.close()
    }
}

class CIOTransaction(override val connection: CIOConnection, private val sqlConnection: SqlConnection) :
    Transaction {

    override suspend fun executeStatement(sql: String): Response = executeStatement(SQLStatement(sql, emptyList()))


    override suspend fun executeStatement(statement: SQLStatement): Response {
        connection.monitor.beforeStatement(this, statement)
        var sql = statement.sql
        for (argument in statement.arguments) {
            sql = sql.replaceFirst("?", argument.value.toString())
        }
        
        try {
            val queryResult = sqlConnection.execute(sql) as? SqlTables ?: return Response.Empty
            val sqlTable = queryResult.receive()
            val rows = sqlTable.map { row -> CIORow(row) }.toList()
            val result = CIOResponse(sqlTable, rows)
            connection.monitor.afterStatement(this, statement, result)
            return result
        } catch (e: Exception) {
            throw CIOExecuteStatementException(sql, e)
        }
    }

    override suspend fun commit() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun rollback() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun <T> executeStatement(statement: Statement<T>): T {
        val statementSQL = connection.dialect.statementSQL(statement)
        val result = executeStatement(statementSQL)
        return resultFor(result, statement)
    }


    @Suppress("UNCHECKED_CAST")
    private fun <T> resultFor(response: Response, statement: Statement<T>): T {
        when (statement) {
            is InsertValuesStatement<*, *> -> {
                val keyColumn = statement.generatedKeyColumn
                if (keyColumn == null || response !is CIOResponse) {
                    return Unit as T
                } else {
                    val row = response.singleOrNull() ?: return Unit as T
                    val columnValue = row.columnValue(keyColumn.type.runtimeType, 0)
                    return columnValue as T
                }
            }
            is InsertQueryStatement<*> -> {
                // TODO: support generating sequence for fetch keys
                return emptySequence<Nothing>() as T
            }
            is QueryStatement -> {
                return response as T
            }

            is DeleteQueryStatement<*>,
            is UpdateQueryStatement<*> -> {
                return Unit as T
            }
            else -> error("Cannot extract result for $statement")
        }
    }

    override suspend fun databaseSchema(): DatabaseSchema {
        return CIODatabaseSchema(this)
    }

    override fun createBlob(bytes: ByteArray): BinaryObject {
        return object : BinaryObject {
            override val bytes: ByteArray get() = bytes
        }
    }

    override fun close() {
        sqlConnection.close()
    }

}

class CIOExecuteStatementException(query: String, cause: Exception) :
    Exception("Failed to execute SQL:\n$query", cause)

class CIORow(val row: SqlRow) : ResultRow {
    override fun columnValue(type: KClass<*>, columnName: String, tableName: String?): Any? {
        val index = row.columns.indexOfFirst { it.name == columnName }
        if (index == -1) return null
        return columnValue(type, index)

    }

    override fun columnValue(type: KClass<*>, index: Int): Any? {
        val cell = row[index]
        return when (type) {
            String::class -> cell.asString()
            else -> null
        }
    }

}

class CIOResponse(private val table: SqlTable, private val rows: List<CIORow>) : Response {
    val columns get() = table.columns
    override fun iterator(): Iterator<ResultRow> = rows.iterator()
}
