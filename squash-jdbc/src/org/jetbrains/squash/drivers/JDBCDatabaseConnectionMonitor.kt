package org.jetbrains.squash.drivers

import org.jetbrains.squash.connection.*
import org.jetbrains.squash.dialect.*

class JDBCDatabaseConnectionMonitor : DatabaseConnectionMonitor {
    private val beforeCallbacks = mutableListOf<Transaction.(SQLStatement) -> Unit>()
    private val afterCallbacks = mutableListOf<Transaction.(SQLStatement, result: Any?) -> Unit>()

    override fun before(callback: Transaction.(SQLStatement) -> Unit) {
        beforeCallbacks.add(callback)
    }

    override fun after(callback: Transaction.(SQLStatement, Any?) -> Unit) {
        afterCallbacks.add(callback)
    }

    fun beforeStatement(transaction: Transaction, statement: SQLStatement) {
        var exceptions : MutableList<Throwable>? = null
        beforeCallbacks.forEach {
            try {
                transaction.it(statement)
            } catch (t: Throwable) {
                if (exceptions == null)
                    exceptions = arrayListOf()
                exceptions!!.add(t)
            }
        }
        exceptions?.let { exceptions ->
            if (exceptions.size == 1)
                throw exceptions[0]
            else {
                val throwable = Throwable("Multiple exceptions occurred in `before` callbacks")
                exceptions.forEach { throwable.addSuppressed(it) }
                throw throwable
            }
        }
    }

    fun afterStatement(transaction: Transaction, statement: SQLStatement, result: Any?) {
        var exceptions : MutableList<Throwable>? = null
        afterCallbacks.forEach {
            try {
                transaction.it(statement, result)
            } catch (t: Throwable) {
                if (exceptions == null)
                    exceptions = arrayListOf()
                exceptions!!.add(t)
            }
        }
        exceptions?.let { exceptions ->
            if (exceptions.size == 1)
                throw exceptions[0]
            else {
                val throwable = Throwable("Multiple exceptions occurred in in `after` callbacks")
                exceptions.forEach { throwable.addSuppressed(it) }
                throw throwable
            }
        }
    }
}