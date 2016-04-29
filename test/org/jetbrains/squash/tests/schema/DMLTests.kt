package org.jetbrains.squash.tests.schema

import org.jetbrains.squash.*
import org.jetbrains.squash.statements.*
import org.jetbrains.squash.tests.*
import org.jetbrains.squash.tests.data.CitiesSchema.Cities
import org.jetbrains.squash.tests.data.CitiesSchema.Citizens
import org.jetbrains.squash.tests.data.CitiesSchema.UserData
import org.junit.*
import kotlin.test.*

class DMLTests {
    fun withCitiesAndUsers(statement: Transaction.() -> Unit) {
        withTables(Cities, Citizens, UserData) {
            val saintPetersburgId = insertInto(Cities).fetch(Cities.id) {
                it[name] = "St. Petersburg"
            }

            val munichId = insertInto(Cities).fetch(Cities.id) {
                it[name] = "Munich"
            }

            insertInto(Cities) {
                it[name] = "Prague"
            }

            insertInto(Citizens) {
                it[id] = "andrey"
                it[name] = "Andrey"
                it[cityId] = saintPetersburgId
            }

            insertInto(Citizens) {
                it[id] = "sergey"
                it[name] = "Sergey"
                it[cityId] = munichId
            }

            insertInto(Citizens) {
                it[id] = "eugene"
                it[name] = "Eugene"
                it[cityId] = munichId
            }

            insertInto(Citizens) {
                it[id] = "alex"
                it[name] = "Alex"
                it[cityId] = null
            }

            insertInto(Citizens) {
                it[id] = "smth"
                it[name] = "Something"
                it[cityId] = null
            }

            insertInto(UserData) {
                it[user_id] = "smth"
                it[comment] = "Something is here"
                it[value] = 10
            }

            insertInto(UserData) {
                it[user_id] = "eugene"
                it[comment] = "Comment for Eugene"
                it[value] = 20
            }

            insertInto(UserData) {
                it[user_id] = "sergey"
                it[comment] = "Comment for Sergey"
                it[value] = 30
            }
            statement()
        }
    }

    @Test fun testBuildTestData() {
        withCitiesAndUsers {}
    }

/*
    @Test fun testPreparedStatement() {
        withCitiesAndUsers {
            val name = users.select{users.id eq "eugene"}.first()[users.name]
            assertEquals("Eugene", name)
        }
    }
*/

}