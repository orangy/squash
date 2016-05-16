package org.jetbrains.squash.dialects.h2.tests

import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.statements.*
import org.jetbrains.squash.tests.data.*
import org.junit.*
import kotlin.test.*

class QueryBuilderTests {
    @Test fun selectLiteral() {
        withTables() {
            val eugene = literal("eugene")
            val query = query().select { eugene }
            val sql = connection.dialect.querySQL(query)
            assertEquals("SELECT ?", sql.sql)
            val row = query.execute().rows.single()
            assertEquals(eugene.literal, row.get<String>("?1"))
        }
    }

    @Test fun selectFromWhere() {
        withCities {
            val eugene = literal("eugene")
            val query = query().from(Citizens)
                    .where { Citizens.id eq eugene }
                    .select(Citizens.name, Citizens.id)
            val sql = connection.dialect.querySQL(query)
            assertEquals("SELECT Citizens.name, Citizens.id FROM Citizens WHERE Citizens.id = ?", sql.sql)
            val row = query.execute().rows.single()
            assertEquals("eugene", row[Citizens.id])
            assertEquals("Eugene", row[Citizens.name])
        }
    }

    @Test fun selectOperationFromWhere() {
        withCities {
            val eugene = literal("eugene")
            val query = query().from(Citizens)
                    .where { Citizens.id eq eugene }
                    .select { (Citizens.cityId + 1).alias("first") }
                    .select { Citizens.cityId - 1 }
                    .select { Citizens.cityId / 1 }
                    .select { Citizens.cityId * 1 }
            val sql = connection.dialect.querySQL(query)
            assertEquals("SELECT Citizens.city_id + ? AS first, Citizens.city_id - ?, Citizens.city_id / ?, Citizens.city_id * ? FROM Citizens WHERE Citizens.id = ?", sql.sql)
            val row = query.execute().rows.single()
            assertEquals(3, row.get<Int>("first"))
        }
    }

    @Test fun selectFromWhereSubQuery() {
        withTables {
            val eugene = literal("eugene")
            val query = query().from(Citizens)
                    .where { Citizens.id eq subquery<String> { from(Citizens).select { Citizens.id }.where { Citizens.id eq eugene } } }
                    .select { Citizens.name }
            val sql = connection.dialect.querySQL(query)
            assertEquals("SELECT Citizens.name FROM Citizens WHERE Citizens.id = (SELECT Citizens.id FROM Citizens WHERE Citizens.id = ?)", sql.sql)
        }
    }

    @Test fun selectFromWhereWhere() {
        withTables {
            val eugene = literal("eugene")
            val query = query().from(Citizens)
                    .select { Citizens.name }
                    .where { Citizens.id eq eugene }
                    .where { Citizens.cityId eq 1 }
            val sql = connection.dialect.querySQL(query)
            assertEquals("SELECT Citizens.name FROM Citizens WHERE Citizens.id = ? AND Citizens.city_id = ?", sql.sql)
        }
    }

    @Test fun selectFromJoin() {
        withTables {
            val query = query().from(Citizens).innerJoin(Cities) { Cities.id eq Citizens.cityId }
                    .select { Citizens.name }.select { Cities.name }
            val sql = connection.dialect.querySQL(query)
            assertEquals("SELECT Citizens.name, Cities.name FROM Citizens INNER JOIN Cities ON Cities.id = Citizens.city_id", sql.sql)
        }
    }

    @Test fun selectFromJoinAliased() {
        withCities {
            val citizenName = Citizens.name.alias("citizenName")
            val cityName = Cities.name.alias("city")
            val query = query()
                    .from(Citizens)
                    .innerJoin(Cities) { Cities.id eq Citizens.cityId }
                    .select(citizenName, cityName)
            val sql = connection.dialect.querySQL(query)
            assertEquals("SELECT Citizens.name AS citizenName, Cities.name AS city FROM Citizens INNER JOIN Cities ON Cities.id = Citizens.city_id", sql.sql)
            val rows = query.execute().rows.toList()
            assertEquals(3, rows.size)
            assertEquals("Andrey", rows[0][citizenName])
            assertEquals("St. Petersburg", rows[0][cityName])
            assertEquals("Sergey", rows[1][citizenName])
            assertEquals("Munich", rows[1][cityName])
            assertEquals("Eugene", rows[2][citizenName])
            assertEquals("Munich", rows[2][cityName])
        }
    }

    @Test fun selectFromJoinJoin() {
        withTables {
            val query = query().from(Citizens)
                    .innerJoin(Cities) { Cities.id eq Citizens.cityId }
                    .innerJoin(CitizenData) { Citizens.id eq CitizenData.citizen_id }
                    .select { Citizens.name }.select { Cities.name }
            val sql = connection.dialect.querySQL(query)
            assertEquals("SELECT Citizens.name, Cities.name FROM Citizens INNER JOIN Cities ON Cities.id = Citizens.city_id INNER JOIN CitizenData ON Citizens.id = CitizenData.Citizens_id", sql.sql)
        }
    }

    @Test fun typedQuery() {
        withTables {
            val sql = connection.dialect.querySQL(Inhabitants)
            assertEquals("SELECT Citizens.name AS citizenName, Cities.name AS cityName FROM Citizens INNER JOIN Cities ON Cities.id = Citizens.city_id", sql.sql)
        }
    }

    @Test fun typedQueryAltered() {
        withTables {
            val sql = connection.dialect.querySQL(Inhabitants.where { Inhabitants.citizenName eq "eugene" })
            assertEquals("SELECT Citizens.name AS citizenName, Cities.name AS cityName FROM Citizens INNER JOIN Cities ON Cities.id = Citizens.city_id WHERE citizenName = ?", sql.sql)
        }
    }
}
