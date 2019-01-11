/*
package org.jetbrains.squash.drivers.cio

import io.ktor.experimental.client.postgre.*
import org.jetbrains.squash.connection.*
import org.jetbrains.squash.dialect.*

open class CIOConnection(override val dialect: SQLDialect) : DatabaseConnection {
    val client = PostgreClient(address, database, user, password)

    override val monitor = DefaultDatabaseConnectionMonitor()

    override fun createTransaction() = CIOTransaction(this, client.connection())
    override fun close() {}
}

class CIOTransaction(
    override val connection: DatabaseConnection, private val sqlConnection: SqlConnection

) :
    Transaction {

}
*/
