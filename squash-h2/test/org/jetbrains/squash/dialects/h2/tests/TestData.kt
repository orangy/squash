package org.jetbrains.squash.dialects.h2.tests

import org.jetbrains.squash.*
import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.statements.*
import org.jetbrains.squash.tests.data.*

fun withCitiesAndUsers(statement: Transaction.() -> Unit) {
    withTables(Cities, Citizens, CitizenData) {

        val spbId = insertInto(Cities).values {
            it[name] = "St. Petersburg"
        }.fetch(Cities.id).execute()

        val munichId = insertInto(Cities).values {
            it[name] = "Munich"
        }.fetch(Cities.id).execute()

        insertInto(Cities).values {
            it[name] = "Prague"
        }.execute()

        insertInto(Cities).from(Cities).where { Cities.name eq "Prague" }.select(Cities.id).execute()

        insertInto(Citizens).values {
            it[id] = "andrey"
            it[name] = "Andrey"
            it[cityId] = spbId
        }.execute()

        insertInto(Citizens).values {
            it[id] = "sergey"
            it[name] = "Sergey"
            it[cityId] = munichId
        }.execute()

        insertInto(Citizens).values {
            it[id] = "eugene"
            it[name] = "Eugene"
            it[cityId] = munichId
        }.execute()

        insertInto(Citizens).values {
            it[id] = "alex"
            it[name] = "Alex"
            it[cityId] = null
        }.execute()

        insertInto(Citizens).values {
            it[id] = "smth"
            it[name] = "Something"
            it[cityId] = null
        }.execute()

        insertInto(CitizenData).values {
            it[citizen_id] = "smth"
            it[comment] = "Something is here"
            it[value] = 10
        }.execute()

        insertInto(CitizenData).values {
            it[citizen_id] = "eugene"
            it[comment] = "Comment for Eugene"
            it[value] = 20
        }.execute()

        insertInto(CitizenData).values {
            it[citizen_id] = "sergey"
            it[comment] = "Comment for Sergey"
            it[value] = 30
        }.execute()

        statement()
    }
}
