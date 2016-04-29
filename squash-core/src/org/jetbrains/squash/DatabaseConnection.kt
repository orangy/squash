package org.jetbrains.squash

interface DatabaseConnection : AutoCloseable {
    val dialect: SQLDialect
    fun createTransaction(): Transaction
}
