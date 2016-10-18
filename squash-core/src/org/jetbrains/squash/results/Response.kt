package org.jetbrains.squash.results

/**
 * Represents a response from the database server, being essentially [Sequence] of [ResultRow]
 */
interface Response : Sequence<ResultRow> {
    companion object {
        val Empty = object : Response {
            val sequence = emptySequence<ResultRow>()
            override fun iterator(): Iterator<ResultRow> = sequence.iterator()
        }
    }
}