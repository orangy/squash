package org.jetbrains.squash.dialects.sqlite

import org.jetbrains.squash.connection.*
import org.jetbrains.squash.drivers.*
import java.sql.*

class SqLiteConnection(connector: () -> Connection) : JDBCConnection(SqLiteDialect, SqLiteDataConversion(), connector) {
    override fun createTransaction(): Transaction = SqLiteTransaction(this)

    companion object {
        fun create(url: String, user: String = "", password: String = ""): DatabaseConnection {
            Class.forName("org.sqlite.JDBC").newInstance()
            val jdbcUrl = "jdbc:sqlite://$url"
            return SqLiteConnection { DriverManager.getConnection(jdbcUrl, user, password) }
        }

        fun createMemoryConnection(user: String = "", password: String = ""): DatabaseConnection {
            Class.forName("org.sqlite.JDBC").newInstance()
            val jdbcUrl = "jdbc:sqlite::memory:"
            return SqLiteConnection { DriverManager.getConnection(jdbcUrl, user, password) }
        }
    }
}

