package org.jetbrains.squash.dialects.h2.tests

import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.tests.data.*
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
            assertEquals("SELECT Citizens.name FROM Citizens WHERE Citizens.id = 'eugene'", sql.sql)
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
            assertEquals("SELECT Citizens.name FROM Citizens WHERE Citizens.id = 'eugene' AND Citizens.city_id = 1", sql.sql)
        }
    }

    @Test fun selectFromJoin() {
        withTables {
            val query = query.from(Citizens).innerJoin(Cities) { Cities.id eq Citizens.cityId }
                    .select { Citizens.name }.select { Cities.name }
            val sql = connection.dialect.querySQL(query)
            assertEquals("SELECT Citizens.name, Cities.name FROM Citizens INNER JOIN Cities ON Cities.id = Citizens.city_id", sql.sql)
        }
    }

    @Test fun selectFromJoinJoin() {
        withTables {
            val query = query.from(Citizens)
                    .innerJoin(Cities) { Cities.id eq Citizens.cityId }
                    .innerJoin(CitizenData) { Citizens.id eq CitizenData.citizen_id }
                    .select { Citizens.name }.select { Cities.name }
            val sql = connection.dialect.querySQL(query)
            assertEquals("SELECT Citizens.name, Cities.name FROM Citizens INNER JOIN Cities ON Cities.id = Citizens.city_id INNER JOIN CitizenData ON Citizens.id = CitizenData.Citizens_id", sql.sql)
        }
    }
}
