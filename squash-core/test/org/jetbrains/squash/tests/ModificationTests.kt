package org.jetbrains.squash.tests

import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.query.*
import org.jetbrains.squash.statements.*
import org.jetbrains.squash.tests.data.*
import org.junit.*
import java.math.*
import java.time.*
import java.util.*
import kotlin.test.*

abstract class ModificationTests : DatabaseTests {
    @Test fun testBuildTestData() {
        withCities {}
    }

    @Test fun insertAllColumnsTable() {
        withTables(AllColumnTypes) {
            insertInto(AllColumnTypes).values {
                it[varchar] = "varchar"
                it[char] = 'c'
                it[enum] = E.ONE
                it[decimal] = BigDecimal.ONE
                it[long] = 222L
                it[date] = LocalDate.of(1976, 11, 24)
                it[bool] = true
                it[datetime] = LocalDateTime.of(LocalDate.of(1976, 11, 24), LocalTime.of(8, 22))
                it[text] = "Lorem Ipsum"
                it[binary] = byteArrayOf(1,2,3)
                it[uuid]= UUID.fromString("7cb64fe4-4938-4e88-8d94-17e929d40c99")
            }
        }
    }

    @Test fun updateSingleRow() {
        withCities {
            val alexId = "alex"
            val oldName = "Alex"
            val newName = "Alexey"
            val query = query().from(Citizens).select(Citizens.name).where { Citizens.id eq alexId }

            val alexName = query.execute().first()[Citizens.name]
            assertEquals(oldName, alexName);

            update(Citizens).where { Citizens.id eq alexId }.set {
                it[Citizens.name] = newName
            }.execute()

            // update(Citizens).where { Citizens.id eq alexId }.set(Citizens.name, newName).execute()

            val alexNewName = query.execute().first()[Citizens.name]
            assertEquals(newName, alexNewName);
        }
    }

    @Test fun deleteAll() {
        withCities {
            deleteFrom(CitizenData).execute()
            val exists = query().from(CitizenData).execute().any()
            assertEquals(false, exists)
        }
    }

    @Test fun deleteWhereLike() {
        withCities {
            val query = query().from(Citizens).select(Citizens.id).where { Citizens.name like "%thing" }
            val smthId = query.execute().single()[Citizens.id]
            assertEquals ("smth", smthId)

            deleteFrom(Citizens).where { Citizens.name like "%thing" }.execute()

            val hasSmth = query.execute().any()
            assertEquals(false, hasSmth)
        }
    }

}