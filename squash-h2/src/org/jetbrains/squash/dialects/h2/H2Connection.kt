package org.jetbrains.squash.dialects.h2

import org.h2.jdbcx.*
import org.jetbrains.squash.connection.*
import org.jetbrains.squash.drivers.*
import java.sql.*

class H2Connection(connector: () -> Connection) : JDBCConnection(H2Dialect, H2DataConversion(), connector) {
    override fun createTransaction() = H2Transaction(this)

    companion object {
        fun create(url: String, user: String = "", password: String = ""): DatabaseConnection {
            require(url.startsWith("jdbc:h2:")) { "H2 JDBC connection requires 'jdbc:h2:' prefix" }
            Class.forName("org.h2.Driver").newInstance()
            return H2Connection { DriverManager.getConnection(url, user, password) }
        }

        fun createMemoryConnection(catalogue: String = "", user: String = "", password: String = ""): DatabaseConnection {
            val pool = JdbcConnectionPool.create("jdbc:h2:mem:$catalogue", user, password);
            return H2Connection { pool.connection }
        }
    }
}

