package org.jetbrains.squash.dialects.postgres

import org.jetbrains.squash.dialect.*
import org.jetbrains.squash.drivers.*
import org.jetbrains.squash.schema.*
import org.jetbrains.squash.results.*

class PgDatabaseSchema(dialect: SQLDialect, transaction: JDBCTransaction) : JDBCDatabaseSchema(transaction) {
    override fun tables(): Sequence<DatabaseSchema.SchemaTable> {
        val schema = currentSchema()
        val tableTypes = if (schema.startsWith("pg_temp_")) arrayOf("TEMPORARY TABLE") else arrayOf("TABLE")
        val resultSet = metadata.getTables(catalogue, currentSchema(), null, tableTypes)
        return JDBCResponse(transaction.connection.conversion, resultSet).rows.map { SchemaTable(it["TABLE_NAME"], this) }
    }
}