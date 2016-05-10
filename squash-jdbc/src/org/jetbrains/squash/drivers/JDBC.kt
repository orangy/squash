package org.jetbrains.squash.drivers

import org.jetbrains.squash.*
import org.jetbrains.squash.definition.*
import org.jetbrains.squash.dialect.*
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
        statement.forEachParameter { column, value ->
            val index = statementSQL.indexes[column] ?: error("${connection.dialect} didn't provide index for column '$column'")
            preparedStatement.prepareValue(index, column.type, value)
        }
        preparedStatement.execute()
        return preparedStatement.resultFor(statement)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> PreparedStatement.resultFor(statement: Statement<T>): T {
        when (statement) {
            is InsertStatement<*, *> -> {
                val column = statement.generatedKeyColumn ?: return Unit as T
                val result = generatedKeys.apply { next() }
                return result.extractValueForColumn(0, column) as T
            }
            else -> error("Cannot extract result for $statement")
        }
    }

    fun ResultSet.extractValueForColumn(index: Int, column: Column<Any?>): Any? {
        when (column.type) {
            is IntColumnType -> return getInt(index + 1)
            is StringColumnType -> return getString(index + 1)
            is LongColumnType -> return getLong(index + 1)
            else -> error("Cannot extract result for $column")
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


class JDBCDatabaseSchema(private val connection: Connection) : DatabaseSchema {
    private val catalogue: String = connection.catalog
    private val metadata: DatabaseMetaData = connection.metaData

    override fun tables(): Sequence<DatabaseSchema.Table> {
        return metadata.getTables(catalogue, null, null, arrayOf("TABLE")).asSequence().map {
            Table(it.getString("TABLE_NAME"), this)
        }
    }

    class Column(override val name: String,
                 override val nullable: Boolean,
                 override val autoIncrement: Boolean,
                 override val size: Int) : DatabaseSchema.Column {
        override fun toString(): String = "[JDBC] Column: $name"
    }

    class Table(override val name: String, private val schema: JDBCDatabaseSchema) : DatabaseSchema.Table {
        override fun columns(): Sequence<DatabaseSchema.Column> {
            return schema.metadata.getColumns(schema.catalogue, null, name, null).asSequence().map {
                //val dataType = it.getInt("DATA_TYPE")
                val columnSize = it.getInt("COLUMN_SIZE")
                val autoIncrement = it.getString("IS_AUTOINCREMENT") == "YES"
                val columnName = it.getString("COLUMN_NAME")
                val nullable = it.getBoolean("NULLABLE")
                Column(columnName, nullable, autoIncrement, columnSize)
            }
        }

        override fun toString(): String = "[JDBC] Table: $name"
    }
}


class JDBCConnection(override val dialect: SQLDialect, val connector: () -> Connection) : DatabaseConnection {
    override fun close() {

    }

    override fun createTransaction(): Transaction = JDBCTransaction(this, connector)

    companion object {
        fun create(dialect: SQLDialect, url: String, driver: String, user: String = "", password: String = ""): DatabaseConnection {
            Class.forName(driver).newInstance()
            return JDBCConnection(dialect) { DriverManager.getConnection(url, user, password) }
        }
    }
}

fun ResultSet.asSequence() = ResultSetSequence(this)
class ResultSetSequence(val resultSet: ResultSet) : Sequence<ResultSet> {
    override operator fun iterator(): Iterator<ResultSet> = object : Iterator<ResultSet> {
        override fun hasNext(): Boolean = resultSet.next()
        override fun next(): ResultSet = resultSet
    }
}