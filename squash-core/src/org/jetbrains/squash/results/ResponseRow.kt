package org.jetbrains.squash.results

import org.jetbrains.squash.definition.*

interface Response {
    val rows: Sequence<ResponseRow>

    companion object {
        val Empty = object : Response {
            override val rows: Sequence<ResponseRow>
                get() = emptySequence()

        }
    }
}

interface ResponseRow {
    operator fun <V> get(column: Column<V>): V
    fun <V> get(name: String): V
}