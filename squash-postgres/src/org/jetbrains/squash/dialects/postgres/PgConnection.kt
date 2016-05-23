package org.jetbrains.squash.dialects.postgres

import org.jetbrains.squash.connection.*
import org.jetbrains.squash.drivers.*
import java.sql.*

class PgConnection(connector: () -> Connection) : JDBCConnection(PgDialect, PgDataConversion(), connector) {
    override fun createTransaction(): Transaction = PgTransaction(this)

    companion object {
        fun create(url: String, user: String = "", password: String = ""): DatabaseConnection {
            Class.forName("org.postgresql.Driver").newInstance()
            val jdbcUrl = "jdbc:postgresql://$url"
            return PgConnection { DriverManager.getConnection(jdbcUrl, user, password) }
        }
    }
}

