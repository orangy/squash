package org.jetbrains.squash.dialects.h2.tests

import org.jetbrains.squash.*
import org.jetbrains.squash.statements.*
import org.jetbrains.squash.tests.data.*

fun withCitiesAndUsers(statement: Transaction.() -> Unit) {
    withTables(Cities, Citizens, CitizenData) {
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

        insertInto(CitizenData) {
            it[citizen_id] = "smth"
            it[comment] = "Something is here"
            it[value] = 10
        }

        insertInto(CitizenData) {
            it[citizen_id] = "eugene"
            it[comment] = "Comment for Eugene"
            it[value] = 20
        }

        insertInto(CitizenData) {
            it[citizen_id] = "sergey"
            it[comment] = "Comment for Sergey"
            it[value] = 30
        }
        statement()
    }
}
