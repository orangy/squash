package org.jetbrains.squash.dialects.postgres

import org.jetbrains.squash.connection.*
import org.jetbrains.squash.drivers.*
import java.sql.*

class PgConnection(connector: () -> Connection) : JDBCConnection(PgDialect, PgDataConversion(), connector) {
    override fun createTransaction() = PgTransaction(this)

    companion object {
        private val driver = Class.forName("org.postgresql.Driver").getConstructor().newInstance()

        fun create(host: String, user: String = "", password: String = ""): DatabaseConnection {
            val jdbcUrl = "jdbc:postgresql://$host"
            return PgConnection { DriverManager.getConnection(jdbcUrl, user, password) }
        }

        fun create(url: String): DatabaseConnection {
            return PgConnection { DriverManager.getConnection(url) }
        }
    }
}

