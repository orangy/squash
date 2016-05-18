package org.jetbrains.squash.drivers

import org.jetbrains.squash.*
import org.jetbrains.squash.definition.*
import java.sql.*

open class JDBCDatabaseSchema(val connection: Connection) : DatabaseSchema {
    protected val catalogue: String = connection.catalog
    protected val metadata: DatabaseMetaData = connection.metaData

    override fun tables(): Sequence<DatabaseSchema.Table> {
        val resultSet = metadata.getTables(catalogue, currentSchema(), null, arrayOf("TABLE"))
        return JDBCResponse(resultSet).rows.map { Table(it.get<String>("TABLE_NAME"), this) }
    }

    protected open fun currentSchema() : String = connection.schema

    class Column(override val name: String,
                 override val nullable: Boolean,
                 override val autoIncrement: Boolean,
                 override val size: Int) : DatabaseSchema.Column {
        override fun toString(): String = "[JDBC] Column: $name"
    }

    class Table(override val name: String, private val schema: JDBCDatabaseSchema) : DatabaseSchema.Table {
        override fun columns(): Sequence<DatabaseSchema.Column> {
            val resultSet = schema.metadata.getColumns(schema.catalogue, schema.currentSchema(), name, null)
            val response = JDBCResponse(resultSet)
            return response.rows.map {
                val columnSize = it.get<Int>("COLUMN_SIZE")
                val autoIncrement = it.get<String>("IS_AUTOINCREMENT") == "YES"
                val columnName = it.get<String>("COLUMN_NAME")
                val nullable = it.get<Int>("NULLABLE") == DatabaseMetaData.columnNullable
                Column(columnName, nullable, autoIncrement, columnSize)
            }
        }

        override fun toString(): String = "[JDBC] Table: $name"
    }
}