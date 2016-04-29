package org.jetbrains.squash.statements

import org.jetbrains.squash.*

interface Statement<T> {
    fun forEachParameter(body: (Column<*>, Any?) -> Unit)
}