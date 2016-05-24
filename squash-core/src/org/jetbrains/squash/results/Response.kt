package org.jetbrains.squash.results

/**
 * Represents a response from the database server, being essentially [Sequence] of [ResponseRow]
 */
interface Response : Sequence<ResponseRow> {
    companion object {
        val Empty = object : Response {
            val sequence = emptySequence<ResponseRow>()
            override fun iterator(): Iterator<ResponseRow> = sequence.iterator()
        }
    }
}