package org.jetbrains.squash.tests

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.query.*
import org.jetbrains.squash.results.*
import org.jetbrains.squash.statements.*
import org.junit.*

object LoadTable : TableDefinition() {
    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", 20)
    val value = integer("value")
}

abstract class LoadTests : DatabaseTests {
    @Test fun iterationTests() {
        withTables(LoadTable) {
            repeat(10000) { seq ->
                insertInto(LoadTable).values {
                    it[name] = "$seq-value"
                    it[value] = seq
                }.execute()
            }

            repeat(1000) {
                val sum = query(LoadTable).select(LoadTable.name, LoadTable.value).execute().sumBy { it.columnValue(LoadTable.value) }
            }
        }
    }
}
