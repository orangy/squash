package org.jetbrains.squash.tests

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.tests.data.*
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
            executeStatement("CREATE TABLE TEST(ID INT NOT NULL, NAME VARCHAR(255))")
            executeStatement("INSERT INTO TEST (ID, NAME) VALUES (1, 'test')")

            val schema = databaseSchema()
            val tables = schema.tables().toList()
            assertEquals(1, tables.size)
            assertEquals("TEST", tables[0].name.toUpperCase())
            val columns = tables[0].columns().toList()
            assertEquals(2, columns.size)
            assertEquals("ID", columns[0].name.toUpperCase())
            assertEquals("NAME", columns[1].name.toUpperCase())
        }
    }


    @Test
    fun citiesDDL() {
        withCities {
            connection.dialect.definition.tableSQL(Cities).assertSQL {
                "CREATE TABLE IF NOT EXISTS Cities " +
                        "(id ${getIdColumnType(IntColumnType)}, name VARCHAR(50) NOT NULL" +
                        "${autoPrimaryKey("Cities", "id")})"
            }
            connection.dialect.definition.tableSQL(Citizens).assertSQL {
                "CREATE TABLE IF NOT EXISTS Citizens " +
                        "(id VARCHAR(10) NOT NULL, name VARCHAR(50) NOT NULL, city_id INT NULL" +
                        "${primaryKey("Citizens", "id")})"
            }
            connection.dialect.definition.tableSQL(CitizenData).assertSQL {
                """
                    CREATE TABLE IF NOT EXISTS CitizenData (id ${getIdColumnType(LongColumnType)}, comment VARCHAR(30) NOT NULL, ${quote}value${quote} INT NOT NULL, image $blobType NULL${autoPrimaryKey("CitizenData", "id")})
                    CREATE INDEX$indexIfNotExists IX_CitizenData_value ON CitizenData (${quote}value${quote})
                """
            }
            connection.dialect.definition.tableSQL(CitizenDataLink).assertSQL {
                """
                    CREATE TABLE IF NOT EXISTS CitizenDataLink (Citizens_id VARCHAR(10) NOT NULL, CitizenData_id BIGINT NOT NULL${primaryKey("CitizenDataLink_Citizens_id_CitizenData_id", "Citizens_id", "CitizenData_id")})
                """
            }
        }
    }

    @Test
    fun citiesSchema() {
        withTransaction {
            val schema = databaseSchema()
            val tableDefinitions = listOf(Cities, Citizens)
            schema.create(tableDefinitions)
            val validationResult = schema.validate(tableDefinitions)
            if (validationResult.any()) {
                fail(validationResult.joinToString("\n") { it.message })
            }

            val tables = schema.tables().toList()
            assertEquals(2, tables.size)

            with(tables[0]) {
                assertEquals("CITIES", name.toUpperCase())
                val columns = columns().toList()
                assertEquals(2, columns.size)
                with(columns[0]) {
                    assertEquals("ID", name.toUpperCase())
                    assertEquals(false, nullable)
                }
                with(columns[1]) {
                    assertEquals("NAME", name.toUpperCase())
                    assertEquals(false, nullable)
                }
            }

            with(tables[1]) {
                assertEquals("CITIZENS", name.toUpperCase())
                val columns = columns().toList()
                assertEquals(3, columns.size)
                with(columns[0]) {
                    assertEquals("ID", name.toUpperCase())
                    assertEquals(false, nullable)
                }
                with(columns[1]) {
                    assertEquals("NAME", name.toUpperCase())
                    assertEquals(false, nullable)
                }
                with(columns[2]) {
                    assertEquals("CITY_ID", name.toUpperCase())
                    assertEquals(true, nullable)
                }
            }
        }

    }
}
