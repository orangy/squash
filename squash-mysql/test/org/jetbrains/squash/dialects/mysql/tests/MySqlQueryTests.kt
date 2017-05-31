package org.jetbrains.squash.dialects.mysql.tests

import org.jetbrains.squash.tests.*

class MySqlQueryTests : QueryTests(), DatabaseTests by MySqlDatabaseTests() {
    override fun nullsLast(sql: String): String {
        return "ISNULL(${sql.removeSuffix(" DESC")}), $sql"
    }

}