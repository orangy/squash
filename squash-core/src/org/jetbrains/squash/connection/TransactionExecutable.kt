package org.jetbrains.squash.connection

/**
 * Represents an object that can be executed on a [Transaction]
 */
interface TransactionExecutable<R> {
    fun executeOn(transaction: Transaction): R
}