package org.jetbrains.squash.drivers.postgres.jdbc

import org.jetbrains.squash.connection.*
import org.jetbrains.squash.dialects.postgres.*
import org.jetbrains.squash.drivers.jdbc.*
import java.sql.*

class PgJdbcConnection(connector: () -> Connection) : JDBCConnection(
    PgDialect,
    PgDataConversion(), connector) {
    override suspend fun createTransaction() = PgTransaction(this)

    companion object {
        private val driver = Class.forName("org.postgresql.Driver").newInstance()

        fun create(host: String, user: String = "", password: String = ""): DatabaseConnection {
            val jdbcUrl = "jdbc:postgresql://$host"
            return PgJdbcConnection {
                DriverManager.getConnection(
                    jdbcUrl,
                    user,
                    password
                )
            }
        }

        fun create(connectionString: String): DatabaseConnection {
            return PgJdbcConnection { DriverManager.getConnection(connectionString) }
        }
    }
}

