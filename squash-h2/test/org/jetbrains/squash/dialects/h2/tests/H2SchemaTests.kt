package org.jetbrains.squash.dialects.h2.tests

import kotlinx.support.jdk7.*
import org.jetbrains.squash.*
import org.jetbrains.squash.dialects.h2.*
import org.jetbrains.squash.drivers.*
import org.jetbrains.squash.tests.data.*
import org.junit.*
import kotlin.test.*

class H2SchemaTests {
    @Test
    fun testEmptySchema() {
        H2Dialect.createMemoryConnection().use { connection ->
            connection.createTransaction().use { transaction ->
                assertEquals(0, transaction.querySchema().tables().count())
            }
        }
    }

    @Test
    fun testSingleTableSchema() {
        JDBCConnection.create(H2Dialect, "jdbc:h2:mem:", "org.h2.Driver").use { connection ->
            connection.createTransaction().use { transaction ->
                transaction.executeStatement("CREATE TABLE TEST(ID INT AUTO_INCREMENT PRIMARY KEY, NAME VARCHAR(255))")

                val schema = transaction.querySchema()
                val tables = schema.tables().toList()
                assertEquals(1, tables.size)
                assertEquals("TEST", tables[0].name)
                val columns = tables[0].columns().toList()
                assertEquals(2, columns.size)
                assertEquals("ID", columns[0].name)
                assertEquals(true, columns[0].autoIncrement)
                assertEquals(10, columns[0].size)
                assertEquals("NAME", columns[1].name)
                assertEquals(false, columns[1].autoIncrement)
                assertEquals(255, columns[1].size)
            }
        }
    }

    @Test
    fun testCitiesDDL() {
        H2Dialect.createMemoryConnection().use { connection ->
            assertEquals(
                    "CREATE TABLE IF NOT EXISTS Cities (id INT NOT NULL AUTO_INCREMENT, name VARCHAR(50) NOT NULL, CONSTRAINT pk_Cities PRIMARY KEY (id))",
                    connection.dialect.definition.tableSQL(Cities)
            )
            assertEquals(
                    "CREATE TABLE IF NOT EXISTS Citizens (id VARCHAR(10) NOT NULL, name VARCHAR(50) NOT NULL, city_id INT NULL, CONSTRAINT pk_Citizens PRIMARY KEY (id))",
                    connection.dialect.definition.tableSQL(Citizens)
            )
        }
    }

    @Test
    fun testCitiesSchema() {
        H2Dialect.createMemoryConnection().use { connection ->
            val database = Database(connection, listOf(Cities, Citizens))
            database.createSchema()
            val validationResult = database.validateSchema()
            if (validationResult.any()) {
                fail(validationResult.joinToString("\n") { it.message })
            }

            connection.createTransaction().use { transaction ->
                val schema = transaction.querySchema()
                val tables = schema.tables().toList()
                assertEquals(2, tables.size)

                with(tables[0]) {
                    assertEquals("CITIES", name)
                    val columns = columns().toList()
                    assertEquals(2, columns.size)
                    with(columns[0]) {
                        assertEquals("ID", name)
                        assertEquals(true, autoIncrement)
                        assertEquals(false, nullable)
                        assertEquals(10, size)
                    }
                    with(columns[1]) {
                        assertEquals("NAME", name)
                        assertEquals(false, autoIncrement)
                        assertEquals(50, size)
                        assertEquals(false, nullable)
                    }
                }

                with(tables[1]) {
                    assertEquals("CITIZENS", name)
                    val columns = columns().toList()
                    assertEquals(3, columns.size)
                    with(columns[0]) {
                        assertEquals("ID", name)
                        assertEquals(false, autoIncrement)
                        assertEquals(false, nullable)
                        assertEquals(10, size)
                    }
                    with(columns[1]) {
                        assertEquals("NAME", name)
                        assertEquals(false, autoIncrement)
                        assertEquals(50, size)
                        assertEquals(false, nullable)
                    }
                    with(columns[2]) {
                        assertEquals("CITY_ID", name)
                        assertEquals(false, autoIncrement)
                        assertEquals(10, size)
                        assertEquals(true, nullable)
                    }
                }
            }
        }
    }
}
