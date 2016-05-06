package org.jetbrains.squash.dialects.h2.tests

import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.tests.data.CitiesSchema.Citizens
import org.junit.*
import kotlin.test.*

class QueryBuilderTests {
    @Test fun selectLiteral() {
        withTables() {
            val eugene = literal("eugene")
            val query = query.select { eugene }
            val sql = connection.dialect.querySQL(query)
            assertEquals("SELECT 'eugene'", sql.sql)
        }
    }

    @Test fun selectFromWhere() {
        withTables {
            val eugene = literal("eugene")
            val query = query.from(Citizens)
                    .where { Citizens.id eq eugene }
                    .select { Citizens.name }
            val sql = connection.dialect.querySQL(query)
            assertEquals("SELECT name FROM Citizens WHERE id = 'eugene'", sql.sql)
        }
    }

    @Test fun selectFromWhereWhere() {
        withTables {
            val eugene = literal("eugene")
            val query = query.from(Citizens)
                    .where { Citizens.id eq eugene }
                    .where { Citizens.cityId eq 1 }
                    .select { Citizens.name }
            val sql = connection.dialect.querySQL(query)
            assertEquals("SELECT name FROM Citizens WHERE id = 'eugene' AND city_id = 1", sql.sql)
        }
    }
}
