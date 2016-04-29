package org.jetbrains.squash.dialects

import org.h2.jdbcx.*
import org.jetbrains.squash.*
import org.jetbrains.squash.drivers.*

object H2Dialect : BaseSQLDialect() {
    fun createMemoryConnection(catalogue: String = "", user: String = "", password: String = ""): DatabaseConnection {
        val pool = JdbcConnectionPool.create("jdbc:h2:mem:$catalogue", user, password);
        return JDBCConnection(H2Dialect) { pool.connection }
    }

    override fun columnTypeSQL(type: ColumnType): String = when (type) {
        is UUIDColumnType -> "UUID"
        else -> super.columnTypeSQL(type)
    }
}