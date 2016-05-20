package org.jetbrains.squash.dialect

class SQLStatementBuilder() : Appendable {
    val stringBuilder = StringBuilder()
    val arguments = mutableListOf<SQLArgument>()

    fun <V> appendArgument(value: V) {
        append("?")
        val index = arguments.size
        arguments.add(SQLArgument(index, value))
    }

    override fun append(csq: CharSequence): SQLStatementBuilder {
        stringBuilder.append(csq)
        return this
    }

    override fun append(csq: CharSequence, start: Int, end: Int): SQLStatementBuilder {
        stringBuilder.append(csq, start, end)
        return this
    }

    override fun append(c: Char): SQLStatementBuilder {
        stringBuilder.append(c)
        return this
    }

    fun build(): SQLStatement {
        val statementSQL = SQLStatement(stringBuilder.toString(), arguments)
        return statementSQL
    }
}