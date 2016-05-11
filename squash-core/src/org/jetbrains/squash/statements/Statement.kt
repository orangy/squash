package org.jetbrains.squash.statements

import org.jetbrains.squash.definition.*

interface Statement<R> {
    fun forEachArgument(body: (Column<*>, Any?) -> Unit)
}