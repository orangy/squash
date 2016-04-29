package org.jetbrains.squash

import org.jetbrains.squash.dialects.*

interface DatabaseConnection : AutoCloseable {
    val dialect: SQLDialect
    fun createTransaction(): Transaction
}
