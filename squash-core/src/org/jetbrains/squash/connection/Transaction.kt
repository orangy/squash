package org.jetbrains.squash.connection

import org.jetbrains.squash.dialect.*
import org.jetbrains.squash.results.*
import org.jetbrains.squash.schema.*
import org.jetbrains.squash.statements.*

/**
 * Represents an open [Transaction] on a database
 */
interface Transaction : AutoCloseable {
    /**
     * Connection for this [Transaction]
     */
    val connection: DatabaseConnection

    /**
     * Executes [sql] on this [Transaction] and returns a [Response]
     */
    fun executeStatement(sql: String): Response

    /**
     * Executes an SQL [statement] with arguments on this [Transaction] and returns a [Response]
     */
    fun executeStatement(statement: SQLStatement): Response

    /**
     * Commits this [Transaction]
     */
    fun commit()

    /**
     * Rollbacks this [Transaction]
     */
    fun rollback()

    /**
     * Builds and executes structured [statement] on this [Transaction]
     */
    fun <T> executeStatement(statement: Statement<T>): T

    /**
     * Builds and executes structured statement on this [Transaction]
     */
    fun <T> TransactionExecutable<T>.execute(): T = executeOn(this@Transaction)

    /**
     * Fetches database schema on this [Transaction] and returns an instance of [DatabaseSchema]
     */
    fun databaseSchema(): DatabaseSchema
}

interface TransactionExecutable<R> {
    fun executeOn(transaction: Transaction): R
}
