package org.jetbrains.squash.statements

import org.jetbrains.squash.connection.*

interface Statement<R> : TransactionExecutable<R> {
    override suspend fun executeOn(transaction: Transaction): R = transaction.executeStatement(this)
}