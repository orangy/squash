package org.jetbrains.squash

/**
 *
 */
interface Transaction : AutoCloseable {
    val connection : DatabaseConnection
    fun querySchema(): DatabaseSchema

    fun execute(sql: String)
    fun commit()
}

