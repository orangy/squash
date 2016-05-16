package org.jetbrains.squash.dialects.h2.tests

import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.query.*
import org.jetbrains.squash.statements.*
import org.jetbrains.squash.tests.data.*
import org.junit.*
import kotlin.test.*

class QueryTests {
    @Test fun selectLiteral() {
        withTables() {
            val eugene = literal("eugene")
            val query = query().select { eugene }

            connection.dialect.statementSQL(query).assertSQL {
                "SELECT ?"
            }

            val row = query.execute().single()
            assertEquals(eugene.literal, row.get<String>("?1"))
        }
    }

    @Test fun selectFromWhere() {
        withCities {
            val eugene = literal("eugene")
            val query = query().from(Citizens)
                    .where { Citizens.id eq eugene }
                    .select(Citizens.name, Citizens.id)

            connection.dialect.statementSQL(query).assertSQL {
                "SELECT Citizens.name, Citizens.id FROM Citizens WHERE Citizens.id = ?"
            }

            val row = query.execute().single()
            assertEquals("eugene", row[Citizens.id])
            assertEquals("Eugene", row[Citizens.name])
        }
    }

    @Ignore @Test fun selectFromAliasWhere() {
        withCities {
            val eugene = literal("eugene")
            val query = query().from(Citizens.alias("C"))
                    .where { Citizens.id eq eugene }
                    .select(Citizens.name, Citizens.id)

            connection.dialect.statementSQL(query).assertSQL {
                "SELECT C.name, C.id FROM Citizens as C WHERE C.id = ?"
            }

            val row = query.execute().single()
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

            connection.dialect.statementSQL(query).assertSQL {
                "SELECT Citizens.city_id + ? AS first, Citizens.city_id - ?, Citizens.city_id / ?, Citizens.city_id * ? FROM Citizens WHERE Citizens.id = ?"
            }

            val row = query.execute().single()
            assertEquals(3, row.get<Int>("first"))
        }
    }

    @Test fun selectFromWhereSubQuery() {
        withTables {
            val eugene = literal("eugene")
            val query = query().from(Citizens)
                    .where { Citizens.id eq subquery<String> { from(Citizens).select { Citizens.id }.where { Citizens.id eq eugene } } }
                    .select { Citizens.name }

            connection.dialect.statementSQL(query).assertSQL {
                "SELECT Citizens.name FROM Citizens WHERE Citizens.id = (SELECT Citizens.id FROM Citizens WHERE Citizens.id = ?)"
            }
        }
    }

    @Test fun selectFromWhereWhere() {
        withTables {
            val eugene = literal("eugene")
            val query = query().from(Citizens)
                    .select { Citizens.name }
                    .where { Citizens.id eq eugene }
                    .where { Citizens.cityId eq 1 }

            connection.dialect.statementSQL(query).assertSQL {
                "SELECT Citizens.name FROM Citizens WHERE Citizens.id = ? AND Citizens.city_id = ?"
            }
        }
    }

    @Test fun selectFromJoin() {
        withTables {
            val query = query().from(Citizens).innerJoin(Cities) { Cities.id eq Citizens.cityId }
                    .select { Citizens.name }.select { Cities.name }

            connection.dialect.statementSQL(query).assertSQL {
                "SELECT Citizens.name, Cities.name FROM Citizens INNER JOIN Cities ON Cities.id = Citizens.city_id"
            }
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

            connection.dialect.statementSQL(query).assertSQL {
                "SELECT Citizens.name AS citizenName, Cities.name AS city FROM Citizens INNER JOIN Cities ON Cities.id = Citizens.city_id"
            }

            val rows = query.execute().toList()
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

            connection.dialect.statementSQL(query).assertSQL {
                "SELECT Citizens.name, Cities.name FROM Citizens INNER JOIN Cities ON Cities.id = Citizens.city_id INNER JOIN CitizenData ON Citizens.id = CitizenData.Citizens_id"
            }
        }
    }

    @Test fun queryObject() {
        withCities {
            val query = query(Inhabitants)

            connection.dialect.statementSQL(query).assertSQL {
                "SELECT Citizens.name AS citizenName, Cities.name AS cityName FROM Citizens INNER JOIN Cities ON Cities.id = Citizens.city_id"
            }

            val rows = query.execute().toList()
            assertEquals(3, rows.size)
            assertEquals("Andrey", rows[0][Inhabitants.citizenName])
            assertEquals("St. Petersburg", rows[0][Inhabitants.cityName])
            assertEquals("Sergey", rows[1][Inhabitants.citizenName])
            assertEquals("Munich", rows[1][Inhabitants.cityName])
            assertEquals("Eugene", rows[2][Inhabitants.citizenName])
            assertEquals("Munich", rows[2][Inhabitants.cityName])
        }
    }

    @Test fun queryObjectAltered() {
        withCities {
            // TODO: use { Inhabitants.citizenName eq "eugene" }, but WHERE doesn't work on alias
            val query = query(Inhabitants).where { Citizens.name eq "Eugene" }

            connection.dialect.statementSQL(query).assertSQL {
                "SELECT Citizens.name AS citizenName, Cities.name AS cityName FROM Citizens INNER JOIN Cities ON Cities.id = Citizens.city_id WHERE Citizens.name = ?"
            }

            val rows = query.execute().toList()
            assertEquals(1, rows.size)
            assertEquals("Eugene", rows[0][Inhabitants.citizenName])
            assertEquals("Munich", rows[0][Inhabitants.cityName])
        }
    }

    @Test fun queryObjectAlteredTwice() {
        withTables {
            connection.dialect.statementSQL(query(Inhabitants).where { Inhabitants.citizenName eq "eugene" }).assertSQL {
                "SELECT Citizens.name AS citizenName, Cities.name AS cityName FROM Citizens INNER JOIN Cities ON Cities.id = Citizens.city_id WHERE citizenName = ?"
            }
            connection.dialect.statementSQL(query(Inhabitants).where { Inhabitants.cityName eq "Munchen" }).assertSQL {
                "SELECT Citizens.name AS citizenName, Cities.name AS cityName FROM Citizens INNER JOIN Cities ON Cities.id = Citizens.city_id WHERE cityName = ?"
            }
        }
    }

    @Test fun selectFromOrder() {
        withCities {
            val query = query()
                    .from(Citizens)
                    .select(Citizens.name, Citizens.id)
                    .orderBy(Citizens.name)

            connection.dialect.statementSQL(query).assertSQL {
                "SELECT Citizens.name, Citizens.id FROM Citizens ORDER BY Citizens.name"
            }

            val rows = query.execute().map { it[Citizens.name] }.toList()
            assertEquals(listOf("Alex", "Andrey", "Eugene", "Sergey", "Something"), rows)
        }
    }
}
