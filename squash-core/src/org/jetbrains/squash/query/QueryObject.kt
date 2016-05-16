package org.jetbrains.squash.query

import org.jetbrains.squash.statements.*

interface QueryObject {
    fun build(): QueryStatement
}

fun query(queryObject: QueryObject) = queryObject.build()