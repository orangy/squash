package org.jetbrains.squash.dialects.h2.tests

import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.statements.*
import org.jetbrains.squash.tests.data.*
import org.junit.*
import kotlin.test.*

class ModificationTests {
    @Test fun testBuildTestData() {
        withCities {}
    }

    @Test fun testUpdate01() {
        withCities {
            val alexId = "alex"
            val query = query().from(Citizens).select(Citizens.name).where { Citizens.id eq alexId }
            val alexName = query.execute().rows.first()[Citizens.name]
            assertEquals("Alex", alexName);

            val newName = "Alexey"
            update(Citizens).where { Citizens.id eq alexId }.values {
                it[Citizens.name] = newName
            }.execute()

            val alexNewName = query.execute().rows.first()[Citizens.name]
            assertEquals(newName, alexNewName);
        }
    }

}