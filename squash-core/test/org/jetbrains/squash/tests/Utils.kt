package org.jetbrains.squash.tests

import org.jetbrains.squash.*
import org.jetbrains.squash.definition.*

fun <T : Table> Transaction.exists(table: T): Boolean {
    return querySchema().tables().any { String.CASE_INSENSITIVE_ORDER.compare(it.name, table.tableName.id) == 0 }
}