package org.jetbrains.squash.statements

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.query.*
import org.jetbrains.squash.results.*

open class QueryStatement() : QueryBuilder(), Statement<Response>

fun query(): QueryStatement = QueryStatement()

fun Table.where(selector: () -> Expression<Boolean>) = query().from(this).where(selector)
fun <T> Table.select(selector: () -> Expression<T>) = query().from(this).select(selector)
