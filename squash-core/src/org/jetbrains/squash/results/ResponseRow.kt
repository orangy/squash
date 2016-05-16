package org.jetbrains.squash.results

import org.jetbrains.squash.definition.*

interface Response : Sequence<ResponseRow>{
    companion object {
        val Empty = object : Response  {
            val sequence = emptySequence<ResponseRow>()
            override fun iterator(): Iterator<ResponseRow> = sequence.iterator()
        }
    }
}

interface ResponseRow {
    operator fun <V> get(column: Column<V>): V
    fun <V> get(name: String): V
}