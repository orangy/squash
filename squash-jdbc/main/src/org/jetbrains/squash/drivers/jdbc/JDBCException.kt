package org.jetbrains.squash.drivers.jdbc

import org.jetbrains.squash.dialect.*
import java.sql.*

class JDBCException(val exception: SQLException, val statement: SQLStatement)
: SQLException(exception.message, exception.sqlState, exception.errorCode, exception) {
    override fun toString(): String = buildString {
        appendln(exception.toString())
        appendln(statement.sql)
        appendln("Arguments: " + statement.arguments)
    }
}