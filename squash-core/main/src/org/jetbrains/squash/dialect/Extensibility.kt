package org.jetbrains.squash.dialect

interface DialectExtension {
    fun appendTo(builder: SQLStatementBuilder, dialect: SQLDialect)
}
