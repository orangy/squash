package org.jetbrains.squash.tests

import org.jetbrains.squash.connection.*
import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.query.*
import org.jetbrains.squash.results.*
import org.junit.*
import kotlin.test.*

abstract class MonitorTests : DatabaseTests {
    @Test fun selectLiteral() {
        withTables() {
            val eugene = literal("eugene")
            val query = query().select { eugene }

            var beforeTriggered = false
            var afterTriggered = false
            connection.monitor {
                before { sqlStatement ->
                    assertEquals("SELECT ?", sqlStatement.sql)
                    beforeTriggered = true
                }

                after { sqlStatement, result ->
                    assertEquals("SELECT ?", sqlStatement.sql)
                    assertTrue(Response::class.java.isInstance(result))
                    afterTriggered = true
                }
            }

            val row = query.execute().single()
            assertEquals(eugene.literal, row[0])
            assertTrue(beforeTriggered)
            assertTrue(afterTriggered)
        }
    }
}