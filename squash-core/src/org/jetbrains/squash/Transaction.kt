package org.jetbrains.squash

import org.jetbrains.squash.dialect.*
import org.jetbrains.squash.results.*
import org.jetbrains.squash.statements.*

/**
 *
 */
interface Transaction : AutoCloseable {
    val connection: DatabaseConnection
    fun querySchema(): DatabaseSchema

    fun executeStatement(sql: String): Response
    fun executeStatement(statementSQL: SQLStatement): Response

    fun <T> executeStatement(statement: Statement<T>): T

    fun commit()

    fun <T> Statement<T>.execute(): T = executeStatement(this)
}

