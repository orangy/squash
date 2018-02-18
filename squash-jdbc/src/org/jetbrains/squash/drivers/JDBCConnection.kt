package org.jetbrains.squash.drivers

import org.jetbrains.squash.connection.*
import org.jetbrains.squash.dialect.*
import java.sql.*
import javax.sql.*

open class JDBCConnection(override val dialect: SQLDialect, val conversion: JDBCDataConversion, val connector: () -> Connection) : DatabaseConnection {
    override val monitor = JDBCDatabaseConnectionMonitor()

    override fun createTransaction() = JDBCTransaction(this)
    override fun close() {}

    constructor(dialect: SQLDialect, conversion: JDBCDataConversion, dataSource: DataSource) : this(dialect, conversion, { dataSource.connection })

    companion object {
        fun create(dialect: SQLDialect, url: String, driver: String, user: String = "", password: String = ""): DatabaseConnection {
            Class.forName(driver).newInstance()
            return JDBCConnection(dialect, JDBCDataConversion()) {
                DriverManager.getConnection(url, user, password)
            }
        }

        fun create(dialect: SQLDialect, dataSource: DataSource): DatabaseConnection {
            return JDBCConnection(dialect, JDBCDataConversion()) { dataSource.connection }
        }
    }
}