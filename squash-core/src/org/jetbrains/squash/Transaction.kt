package org.jetbrains.squash

import org.jetbrains.squash.dialect.*
import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.results.*
import org.jetbrains.squash.statements.*

/**
 *
 */
interface Transaction : AutoCloseable {
    val connection: DatabaseConnection
    fun querySchema(): DatabaseSchema

    fun executeStatement(sql: String)
    fun <T> executeStatement(statement: Statement<T>): T
    fun executeQuery(query: Query): Response
    fun commit()

    fun executeStatement(statementSQL: SQLStatement)

    fun <T> Statement<T>.execute(): T = executeStatement(this)
    fun Query.execute() : Response =  executeQuery(this)
}

