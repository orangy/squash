package org.jetbrains.squash.tests

import org.jetbrains.squash.*

fun <T : Table> Transaction.exists(table: T): Boolean {
    return querySchema().tables().any { String.CASE_INSENSITIVE_ORDER.compare(it.name, table.tableName) == 0 }
}