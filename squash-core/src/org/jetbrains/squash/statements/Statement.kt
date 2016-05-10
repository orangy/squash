package org.jetbrains.squash.statements

import org.jetbrains.squash.definition.*

interface Statement<R> {
    fun forEachParameter(body: (Column<*>, Any?) -> Unit)
}