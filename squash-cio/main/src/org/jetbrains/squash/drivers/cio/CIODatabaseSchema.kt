package org.jetbrains.squash.drivers.cio

import org.jetbrains.squash.results.*
import org.jetbrains.squash.schema.*

class CIODatabaseSchema(transaction: CIOTransaction) : DatabaseSchemaBase(transaction) {
    override suspend fun tables(): Sequence<DatabaseSchema.SchemaTable> {
        val rows = transaction.executeStatement("select * from information_schema.tables")
        return rows.map {
            CIOSchemaTable(it["table_name"])
        }
    }
}

class CIOSchemaTable(override val name: String) : DatabaseSchema.SchemaTable {
    override fun columns(): Sequence<DatabaseSchema.SchemaColumn> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
