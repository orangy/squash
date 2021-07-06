package org.jetbrains.squash.dialects.sqlite

import org.jetbrains.squash.connection.*
import org.jetbrains.squash.drivers.*
import java.sql.*

class SqLiteConnection(connector: () -> Connection) : JDBCConnection(SqLiteDialect, SqLiteDataConversion(), connector) {
    override fun createTransaction() = SqLiteTransaction(this)

    companion object {
        fun create(url: String, user: String = "", password: String = ""): JDBCConnection {
            Class.forName("org.sqlite.JDBC").getConstructor().newInstance()
            val jdbcUrl = "jdbc:sqlite://$url"
            return SqLiteConnection { DriverManager.getConnection(jdbcUrl, user, password) }
        }

        fun createMemoryConnection(user: String = "", password: String = ""): JDBCConnection {
            Class.forName("org.sqlite.JDBC").getConstructor().newInstance()
            val jdbcUrl = "jdbc:sqlite:file:memdb1?mode=memory&cache=shared"
            return SqLiteConnection { DriverManager.getConnection(jdbcUrl, user, password) }
        }
    }
}

