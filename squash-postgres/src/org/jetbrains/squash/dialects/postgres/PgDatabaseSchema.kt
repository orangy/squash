package org.jetbrains.squash.dialects.postgres

import org.jetbrains.squash.drivers.*
import org.jetbrains.squash.results.*
import org.jetbrains.squash.schema.*

class PgDatabaseSchema(transaction: JDBCTransaction) : JDBCDatabaseSchema(transaction) {
    override fun tables(): Sequence<DatabaseSchema.SchemaTable> {
        val schema = currentSchema()
        val tableTypes = if (schema.startsWith("pg_temp_")) arrayOf("TEMPORARY TABLE") else arrayOf("TABLE")
        val resultSet = metadata.getTables(catalogue, currentSchema(), null, tableTypes)
        val response = JDBCResponse(transaction.connection.conversion, resultSet)
        return response.map { SchemaTable(it["TABLE_NAME"], this) }
    }
}