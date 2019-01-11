package org.jetbrains.squash.connection

import org.jetbrains.squash.dialect.*

/**
 * Represents a connection to a Database with specific [dialect]
 */
interface DatabaseConnection : AutoCloseable {
    /**
     * [SQLDialect] for this connection
     */
    val dialect: SQLDialect

    /**
     * [DatabaseConnectionMonitor] for this connection
     */
    val monitor: DatabaseConnectionMonitor

    /**
     * Creates a [Transaction]
     */
    suspend fun createTransaction(): Transaction
}

suspend inline fun <T> DatabaseConnection.transaction(body: Transaction.() -> T): T = createTransaction().use { it.body() }
