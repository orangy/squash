package org.jetbrains.squash

import org.jetbrains.squash.statements.*

/**
 *
 */
interface Transaction : AutoCloseable {
    val connection: DatabaseConnection
    fun querySchema(): DatabaseSchema

    fun execute(sql: String)
    fun <T> execute(statement: Statement<T>): T
    fun commit()
}

