package org.jetbrains.squash.statements

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.query.*
import org.jetbrains.squash.results.*

open class QueryStatement() : QueryBuilder(), Statement<Response>

fun query(): QueryStatement = QueryStatement()
fun query(table: Table): QueryStatement = QueryStatement().from(table)

