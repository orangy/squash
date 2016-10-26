package org.jetbrains.squash.tests

import org.jetbrains.squash.connection.*
import org.jetbrains.squash.definition.*
import org.jetbrains.squash.graph.*
import org.jetbrains.squash.query.*
import org.jetbrains.squash.results.*
import org.jetbrains.squash.statements.*
import org.junit.*

object LoadTable : TableDefinition() {
    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", 20)
    val value = integer("value")
}

interface Load {
    val name : String
    val value : Int
}

abstract class LoadTests : DatabaseTests {
    private val iterations = 100
    private val rows = 100000

    @Test fun execute() {
        withTables(LoadTable) {
            repeat(rows) { seq ->
                insertInto(LoadTable).values {
                    it[name] = "$seq-value"
                    it[value] = seq
                }.execute()
            }

            repeat(iterations) {
                iterateQuery()
            }
            repeat(iterations) {
                iterateMapping()
            }
        }
    }

    fun Transaction.iterateQuery() {
        query(LoadTable).select(LoadTable.name, LoadTable.value).execute().sumBy { it.columnValue(LoadTable.value) }
    }

    fun Transaction.iterateMapping() {
        query(LoadTable).select(LoadTable.name, LoadTable.value).bind<Load>(LoadTable)
                .execute().sumBy { it.value }
    }
}
