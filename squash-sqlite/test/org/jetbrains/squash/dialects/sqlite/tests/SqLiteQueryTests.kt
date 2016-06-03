package org.jetbrains.squash.dialects.sqlite.tests

import org.jetbrains.squash.tests.*

class SqLiteQueryTests : QueryTests(), DatabaseTests by SqLiteDatabaseTests() {
    override fun nullsLast(sql: String): String {
        return "${sql.removeSuffix(" DESC")} IS NULL, $sql"
    }
}