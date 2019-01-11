package org.jetbrains.squash.tests

import kotlinx.coroutines.*
import org.jetbrains.squash.connection.*
import org.jetbrains.squash.definition.*

fun <T : Table> Transaction.exists(table: T): Boolean = runBlocking {
    val tables = databaseSchema().tables()
    return@runBlocking tables.any { String.CASE_INSENSITIVE_ORDER.compare(it.name, table.compoundName.id) == 0 }
}