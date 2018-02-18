package org.jetbrains.squash.dialects.mysql

import org.jetbrains.squash.connection.*
import org.jetbrains.squash.drivers.*
import java.sql.*

class MySqlConnection(connector: () -> Connection) : JDBCConnection(MySqlDialect, MySqlDataConversion(), connector) {
    override fun createTransaction() = MySqlTransaction(this)

    companion object {
        fun create(url: String, user: String = "", password: String = ""): DatabaseConnection {
            require(url.startsWith("jdbc:mysql:")) { "MySQL JDBC connection requires 'jdbc:mysql:' prefix" }
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance()
            return MySqlConnection { DriverManager.getConnection(url, user, password) }
        }
    }
}
