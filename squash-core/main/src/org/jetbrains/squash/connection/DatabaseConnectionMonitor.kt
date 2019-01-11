package org.jetbrains.squash.connection

import org.jetbrains.squash.dialect.*

interface DatabaseConnectionMonitor {
    fun before(callback: Transaction.(statement: SQLStatement) -> Unit)
    fun after(callback: Transaction.(statement: SQLStatement, result: Any?) -> Unit)
}

inline operator fun DatabaseConnectionMonitor.invoke(configure: DatabaseConnectionMonitor.() -> Unit) = configure()
