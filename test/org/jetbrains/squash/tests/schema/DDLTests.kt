package org.jetbrains.exposed.sql.tests.shared

import kotlinx.support.jdk7.*
import org.jetbrains.squash.*
import org.jetbrains.squash.dialects.*
import org.junit.*
import kotlin.test.*

class DDLTests {
    fun withTables(vararg tables: Table, statement: Transaction.() -> Unit) {
        H2Dialect.createMemoryConnection().use { connection ->
            val database = Database(connection, tables.toList())
            database.createSchema()
            connection.createTransaction().use(statement)
        }
    }

    fun <T : Table> Transaction.exists(table: T): Boolean {
        return querySchema().tables().any { String.CASE_INSENSITIVE_ORDER.compare(it.name, table.tableName) == 0 }
    }


    @Test fun unregisteredTableNotExists() {
        val TestTable = object : Table("test") {
            val id = integer("id").primaryKey()
            val name = varchar("name", length = 42)
        }

        withTables() {
            assertEquals (false, exists(TestTable))
        }
    }

    @Test fun tableExists() {
        val TestTable = object : Table() {
            val id = integer("id").primaryKey()
            val name = varchar("name", length = 42)
        }

        withTables(TestTable) {
            assertEquals (true, exists(TestTable))
        }
    }

/*
    @Test fun unnamedTableWithQuotesSQL() {
        val TestTable = object : Table() {
            val id = integer("id").primaryKey()
            val name = varchar("name", length = 42)
        }

        withTables(TestTable) {
            val q = identityQuoteString
            assertEquals("CREATE TABLE IF NOT EXISTS ${q}unnamedTableWithQuotesSQL\$TestTable$1$q (id INT NOT NULL, name VARCHAR(42) NOT NULL, CONSTRAINT ${q}pk_unnamedTableWithQuotesSQL\$TestTable$1$q PRIMARY KEY (id))", TestTable.ddl)
        }
    }
*/

    @Test fun namedEmptyTableWithoutQuotesSQL() {
        val TestTable = object : Table("test_named_table") {
        }

        withTables(TestTable) {
            val ddl = connection.dialect.tableDefinitionSQL(TestTable)
            assertEquals("CREATE TABLE IF NOT EXISTS test_named_table", ddl)
        }
    }

    @Test fun tableWithDifferentColumnTypesSQL() {
        val TestTable = object : Table("test_table_with_different_column_types") {
            val id = integer("id").autoIncrement()
            val name = varchar("name", 42).primaryKey()
            val age = integer("age").nullable()
            // not applicable in H2 database
            //            val testCollate = varchar("testCollate", 2, "ascii_general_ci")
        }

        withTables(TestTable) {
            val ddl = connection.dialect.tableDefinitionSQL(TestTable)
            assertEquals("CREATE TABLE IF NOT EXISTS test_table_with_different_column_types (id INT NOT NULL AUTO_INCREMENT, name VARCHAR(42) NOT NULL, age INT NULL, CONSTRAINT pk_test_table_with_different_column_types PRIMARY KEY (name))", ddl)
        }
    }

    @Test fun columnsWithDefaults() {
        val TestTable = object : Table("t") {
            val s = varchar("s", 100).default("test")
            val l = long("l").default(42)
        }

        withTables(TestTable) {
            val ddl = connection.dialect.tableDefinitionSQL(TestTable)
            assertEquals("CREATE TABLE IF NOT EXISTS t (s VARCHAR(100) NOT NULL DEFAULT 'test', l BIGINT NOT NULL DEFAULT 42)", ddl)
        }
    }
/*

    @Test fun testIndices01() {
        val t = object : Table("t1") {
            val id = integer("id").primaryKey()
            val name = varchar("name", 255).index()
        }

        withTables(t) {
            val alter = createIndex(t.indices[0].first, t.indices[0].second)
            assertEquals("CREATE INDEX t1_name ON t1 (name)", alter)
        }
    }

    @Test fun testIndices02() {
        val t = object : Table("t2") {
            val id = integer("id").primaryKey()
            val lvalue = integer("lvalue")
            val rvalue = integer("rvalue");
            val name = varchar("name", 255).index()

            init {
                index (false, lvalue, rvalue)
            }
        }

        withTables(t) {
            val a1 = createIndex(t.indices[0].first, t.indices[0].second)
            assertEquals("CREATE INDEX t2_name ON t2 (name)", a1)

            val a2 = createIndex(t.indices[1].first, t.indices[1].second)
            assertEquals("CREATE INDEX t2_lvalue_rvalue ON t2 (lvalue, rvalue)", a2)
        }
    }

    @Test fun testIndices03() {
        val t = object : Table("t1") {
            val id = integer("id").primaryKey()
            val name = varchar("name", 255).uniqueIndex()
        }

        withTables(t) {
            val alter = createIndex(t.indices[0].first, t.indices[0].second)
            assertEquals("CREATE UNIQUE INDEX t1_name_unique ON t1 (name)", alter)

        }
    }

    @Test fun testBlob() {
        val t = object : Table("t1") {
            val id = integer("id").autoIncrement().primaryKey()
            val b = blob("blob")
        }

        withTables(t) {
            val blob = if (currentDialect != PostgreSQLDialect) {
                connection.createBlob().apply {
                    setBytes(1, "Hello there!".toByteArray())
                }
            } else {
                SerialBlob("Hello there!".toByteArray())
            }

            val id = t.insert {
                it[t.b] = blob
            } get (t.id)


            val readOn = t.select { t.id eq id }.first()[t.b]
            val text = readOn.binaryStream.reader().readText()

            assertEquals("Hello there!", text)
        }
    }

    @Test fun testBinary() {
        val t = object : Table() {
            val binary = binary("bytes", 10)
        }

        withTables(t) {
            t.insert { it[t.binary] = "Hello!".toByteArray() }

            val bytes = t.selectAll().single()[t.binary]

            assertEquals("Hello!", String(bytes))

        }
    }
*/

}

