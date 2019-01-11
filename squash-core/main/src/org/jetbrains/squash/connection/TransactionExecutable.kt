package org.jetbrains.squash.connection

/**
 * Represents an object that can be executed on a [Transaction]
 */
interface TransactionExecutable<R> {
    suspend fun executeOn(transaction: Transaction): R
}