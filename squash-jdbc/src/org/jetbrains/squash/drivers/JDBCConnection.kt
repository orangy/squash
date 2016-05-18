package org.jetbrains.squash.drivers

import org.jetbrains.squash.*
import org.jetbrains.squash.dialect.*
import java.sql.*

open class JDBCConnection(override val dialect: SQLDialect, val connector: () -> Connection) : DatabaseConnection {
    override fun createTransaction(): Transaction = JDBCTransaction(this, connector)
    override fun close() { }

    companion object {
        fun create(dialect: SQLDialect, url: String, driver: String, user: String = "", password: String = ""): DatabaseConnection {
            Class.forName(driver).newInstance()
            return JDBCConnection(dialect) {
                DriverManager.getConnection(url, user, password)
            }
        }
    }
}