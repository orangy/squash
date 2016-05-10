package org.jetbrains.squash

import org.jetbrains.squash.statements.*

/**
 *
 */
interface Transaction : AutoCloseable {
    val connection: DatabaseConnection
    fun querySchema(): DatabaseSchema

    fun executeStatement(sql: String)
    //fun executeQuery(sql: String)
    //fun executeQuery(query: Query)
    fun <T> executeStatement(statement: Statement<T>): T
    fun commit()
}

