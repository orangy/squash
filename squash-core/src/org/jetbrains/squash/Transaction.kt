package org.jetbrains.squash

import org.jetbrains.squash.dialect.*
import org.jetbrains.squash.results.*
import org.jetbrains.squash.schema.*
import org.jetbrains.squash.statements.*

/**
 *
 */
interface Transaction : AutoCloseable {
    val connection: DatabaseConnection

    fun executeStatement(sql: String): Response
    fun executeStatement(statementSQL: SQLStatement): Response
    fun commit()

    fun <T> executeStatement(statement: Statement<T>): T
    fun <T> Statement<T>.execute(): T = executeStatement(this)

    fun databaseSchema(): DatabaseSchema
}

