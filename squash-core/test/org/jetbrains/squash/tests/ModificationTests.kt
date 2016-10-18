package org.jetbrains.squash.tests

import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.query.*
import org.jetbrains.squash.results.*
import org.jetbrains.squash.statements.*
import org.jetbrains.squash.tests.data.*
import org.junit.*
import kotlin.test.*

abstract class ModificationTests : DatabaseTests {
    @Test fun testBuildTestData() {
        withCities {}
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
            deleteFrom(CitizenDataLink).execute()
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

            deleteFrom(CitizenDataLink).where { CitizenDataLink.citizen_id like "smth" }.execute()
            deleteFrom(Citizens).where { Citizens.name like "%thing" }.execute()

            val hasSmth = query.execute().any()
            assertEquals(false, hasSmth)
        }
    }

}