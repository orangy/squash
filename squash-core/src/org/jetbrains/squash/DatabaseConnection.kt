package org.jetbrains.squash

import org.jetbrains.squash.dialect.*

interface DatabaseConnection : AutoCloseable {
    val dialect: SQLDialect
    fun createTransaction(): Transaction
}
