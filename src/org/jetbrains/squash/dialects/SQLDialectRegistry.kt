package org.jetbrains.squash.dialects

import java.util.concurrent.*

class SQLDialectRegistry {
    private val dialects = CopyOnWriteArrayList<SQLDialect>()

    init {
        registerDialect(H2Dialect)
    }

    fun registerDialect(dialect: SQLDialect) {
        dialects.add(0, dialect)
    }
}