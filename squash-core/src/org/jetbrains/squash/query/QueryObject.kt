package org.jetbrains.squash.query

import org.jetbrains.squash.statements.*

interface QueryObject {
    fun build(): QueryStatement
}

fun query(q: QueryObject) = q.build()