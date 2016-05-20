package org.jetbrains.squash.tests

import org.jetbrains.squash.tests.data.*
import org.junit.*
import kotlin.test.*

abstract class SchemaTests : DatabaseTests {
    @Test
    fun emptySchema() {
        withTransaction {
            val tables = databaseSchema().tables().toList()
            assertEquals(0, tables.size)
        }
    }

    @Test
    fun singleTableSchema() {
        withTransaction {
            executeStatement("CREATE TABLE TEST(ID $idColumnType PRIMARY KEY, NAME VARCHAR(255))")
            executeStatement("INSERT INTO TEST (NAME) VALUES ('test')")

            val schema = databaseSchema()
            val tables = schema.tables().toList()
            assertEquals(1, tables.size)
            assertEquals("TEST", tables[0].name.toUpperCase())
            val columns = tables[0].columns().toList()
            assertEquals(2, columns.size)
            assertEquals("ID", columns[0].name.toUpperCase())
            assertEquals(true, columns[0].autoIncrement)
            assertEquals(10, columns[0].size)
            assertEquals("NAME", columns[1].name.toUpperCase())
            assertEquals(false, columns[1].autoIncrement)
            assertEquals(255, columns[1].size)
        }
    }


    @Test
    fun citiesDDL() {
        withCities {
            connection.dialect.definition.tableSQL(Cities).assertSQL {
                "CREATE TABLE IF NOT EXISTS Cities (id $idColumnType, name VARCHAR(50) NOT NULL, CONSTRAINT PK_Cities PRIMARY KEY (id))"
            }
            connection.dialect.definition.tableSQL(Citizens).assertSQL {
                "CREATE TABLE IF NOT EXISTS Citizens (id VARCHAR(10) NOT NULL, name VARCHAR(50) NOT NULL, city_id INT NULL, CONSTRAINT PK_Citizens PRIMARY KEY (id))"
            }
        }
    }

    @Test
    fun citiesSchema() {
        withTransaction {
            val schema = databaseSchema()
            val tableDefinitions = listOf(Cities, Citizens)
            schema.create(tableDefinitions, this)
            val validationResult = schema.validate(tableDefinitions, this)
            if (validationResult.any()) {
                fail(validationResult.joinToString("\n") { it.message })
            }

            val tables = schema.tables().toList()
            assertEquals(2, tables.size)

            with(tables[0]) {
                kotlin.test.assertEquals("CITIES", name.toUpperCase())
                val columns = columns().toList()
                kotlin.test.assertEquals(2, columns.size)
                with(columns[0]) {
                    kotlin.test.assertEquals("ID", name.toUpperCase())
                    kotlin.test.assertEquals(true, autoIncrement)
                    kotlin.test.assertEquals(false, nullable)
                    kotlin.test.assertEquals(10, size)
                }
                with(columns[1]) {
                    kotlin.test.assertEquals("NAME", name.toUpperCase())
                    kotlin.test.assertEquals(false, autoIncrement)
                    kotlin.test.assertEquals(50, size)
                    kotlin.test.assertEquals(false, nullable)
                }
            }

            with(tables[1]) {
                kotlin.test.assertEquals("CITIZENS", name.toUpperCase())
                val columns = columns().toList()
                kotlin.test.assertEquals(3, columns.size)
                with(columns[0]) {
                    kotlin.test.assertEquals("ID", name.toUpperCase())
                    kotlin.test.assertEquals(false, autoIncrement)
                    kotlin.test.assertEquals(false, nullable)
                    kotlin.test.assertEquals(10, size)
                }
                with(columns[1]) {
                    kotlin.test.assertEquals("NAME", name.toUpperCase())
                    kotlin.test.assertEquals(false, autoIncrement)
                    kotlin.test.assertEquals(50, size)
                    kotlin.test.assertEquals(false, nullable)
                }
                with(columns[2]) {
                    kotlin.test.assertEquals("CITY_ID", name.toUpperCase())
                    kotlin.test.assertEquals(false, autoIncrement)
                    kotlin.test.assertEquals(10, size)
                    kotlin.test.assertEquals(true, nullable)
                }
            }
        }

    }
}
