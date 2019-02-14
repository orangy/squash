package org.jetbrains.squash.drivers

import org.jetbrains.squash.results.*
import org.jetbrains.squash.schema.*
import java.sql.*

open class JDBCDatabaseSchema(final override val transaction: JDBCTransaction) : DatabaseSchemaBase(transaction) {
    protected val catalogue: String? = transaction.jdbcTransaction.catalog
    protected val metadata: DatabaseMetaData = transaction.jdbcTransaction.metaData

    override fun tables(): Sequence<DatabaseSchema.SchemaTable> {
        val resultSet = metadata.getTables(catalogue, currentSchema(), "%", arrayOf("TABLE"))
        return JDBCResponse(transaction.connection.conversion, resultSet)
            .map { SchemaTable(it["TABLE_NAME"], this) }
    }

    protected open fun currentSchema(): String = transaction.jdbcTransaction.schema ?: ""

    class SchemaColumn(override val name: String,
                       override val nullable: Boolean) : DatabaseSchema.SchemaColumn {
        override fun toString(): String = "[JDBC] Column: $name"
    }

    class SchemaTable(override val name: String, private val schema: JDBCDatabaseSchema) : DatabaseSchema.SchemaTable {
        override fun columns(): Sequence<DatabaseSchema.SchemaColumn> {
            val resultSet = schema.metadata.getColumns(schema.catalogue, schema.currentSchema(), name, "%")
            val response = JDBCResponse(schema.transaction.connection.conversion, resultSet)
            return response.map {
                val columnName = it.get<String>("COLUMN_NAME")
                val nullable = it.get<Int>("NULLABLE") == DatabaseMetaData.columnNullable
                SchemaColumn(columnName, nullable)
            }
        }

        override fun toString(): String = "[JDBC] Table: $name"
    }
}