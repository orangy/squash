package org.jetbrains.squash

/**
 *
 */
interface Transaction : AutoCloseable {
    fun querySchema(): DatabaseSchema

    fun execute(sql: String)
    fun commit()
}

