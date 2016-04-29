package org.jetbrains.squash.drivers

import org.jetbrains.squash.*
import org.jetbrains.squash.dialects.*
import java.sql.*

class JDBCTransaction(val connector: () -> Connection) : Transaction {
    var _connection: Connection? = null
    val connection: Connection get() = _connection ?: run {
        _connection = connector()
        _connection!!
    }

    override fun execute(sql: String) {
        connection.prepareStatement(sql).executeUpdate()
    }

    override fun commit() {
        _connection?.commit()
    }

    override fun querySchema(): DatabaseSchema = JDBCDatabaseSchema(connection)

    override fun close() {
        _connection?.close()
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
                val dataType = it.getInt("DATA_TYPE")
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

    override fun createTransaction(): Transaction = JDBCTransaction(connector)

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