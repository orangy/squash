package org.jetbrains.squash.tests

import org.jetbrains.squash.definition.*
import org.junit.*
import kotlin.test.*

@Suppress("unused")
abstract class DefinitionTests : DatabaseTests {
    @Test fun unregisteredTableNotExists() {
        val TestTable = object : TableDefinition("test") {
            val id = integer("id").primaryKey()
            val name = varchar("name", length = 42)
        }

        withTables() {
            assertEquals (false, exists(TestTable))
        }
    }

    @Test fun tableExists() {
        val TestTable = object : TableDefinition() {
            val id = integer("id").primaryKey()
            val name = varchar("name", length = 42)
        }

        withTables(TestTable) {
            assertEquals (true, exists(TestTable))
        }
    }

    @Test fun unnamedTableWithQuotesSQL() {
        val TestTable = object : TableDefinition() {
            val id = integer("id").primaryKey()
            val name = varchar("name", length = 42)
        }

        withTransaction {
            connection.dialect.definition.tableSQL(TestTable).assertSQL {
                "CREATE TABLE IF NOT EXISTS \"unnamedTableWithQuotesSQL\$TestTable$1\" (id INT NOT NULL, name VARCHAR(42) NOT NULL, CONSTRAINT \"PK_unnamedTableWithQuotesSQL\$TestTable$1\" PRIMARY KEY (id))"
            }
        }
    }

    @Test fun keywordNamedTableWithQuotesSQL() {
        val TestTable = object : TableDefinition("SELECT") {
            val id = integer("FROM").primaryKey()
            val name = varchar("JOIN", length = 42)
        }

        withTransaction {
            connection.dialect.definition.tableSQL(TestTable).assertSQL {
                "CREATE TABLE IF NOT EXISTS \"SELECT\" (\"FROM\" INT NOT NULL, \"JOIN\" VARCHAR(42) NOT NULL, CONSTRAINT PK_SELECT PRIMARY KEY (\"FROM\"))"
            }
        }
    }

    @Test fun namedEmptyTableWithoutQuotesSQL() {
        val TestTable = object : TableDefinition("test_named_table") {
        }

        withTransaction {
            connection.dialect.definition.tableSQL(TestTable).assertSQL {
                "CREATE TABLE IF NOT EXISTS test_named_table"
            }
        }
    }

    @Test fun columnsWithDefaults() {
        val TestTable = object : TableDefinition("t") {
            val s = varchar("s", 100).default("test")
            val l = long("l").default(42)
        }

        withTransaction {
            connection.dialect.definition.tableSQL(TestTable).assertSQL {
                "CREATE TABLE IF NOT EXISTS t (s VARCHAR(100) NOT NULL DEFAULT ?, l BIGINT NOT NULL DEFAULT ?)"
            }
        }
    }

    @Test fun singleColumnIndex() {
        val TestTable = object : TableDefinition("t1") {
            val id = integer("id").primaryKey()
            val name = varchar("name", 255).index()
        }

        withTransaction {
            connection.dialect.definition.tableSQL(TestTable).assertSQL {
                """
                CREATE TABLE IF NOT EXISTS t1 (id INT NOT NULL, name VARCHAR(255) NOT NULL, CONSTRAINT PK_t1 PRIMARY KEY (id))
                CREATE INDEX IF NOT EXISTS IX_t1_name ON t1 (name)
                """
            }
        }
    }

    @Test fun singleColumnUniqueIndex() {
        val TestTable = object : TableDefinition("t1") {
            val id = integer("id").primaryKey()
            val name = varchar("name", 255).uniqueIndex()
        }

        withTransaction {
            connection.dialect.definition.tableSQL(TestTable).assertSQL {
                """
                CREATE TABLE IF NOT EXISTS t1 (id INT NOT NULL, name VARCHAR(255) NOT NULL, CONSTRAINT PK_t1 PRIMARY KEY (id))
                CREATE UNIQUE INDEX IF NOT EXISTS IX_t1_name ON t1 (name)
                """
            }
        }
    }

    @Test fun twoColumnIndex() {
        val TestTable = object : TableDefinition("t2") {
            val id = integer("id").primaryKey()
            val lvalue = integer("lvalue")
            val rvalue = integer("rvalue")

            init {
                index(lvalue, rvalue)
            }
        }

        withTransaction {
            connection.dialect.definition.tableSQL(TestTable).assertSQL {
                """
                CREATE TABLE IF NOT EXISTS t2 (id INT NOT NULL, lvalue INT NOT NULL, rvalue INT NOT NULL, CONSTRAINT PK_t2 PRIMARY KEY (id))
                CREATE INDEX IF NOT EXISTS IX_t2_lvalue_rvalue ON t2 (lvalue, rvalue)
                """
            }
        }
    }

    @Test fun twoIndices() {
        val TestTable = object : TableDefinition("t2") {
            val id = integer("id").primaryKey()
            val lvalue = integer("lvalue").index("one")
            val rvalue = integer("rvalue").index("two")
        }

        withTransaction {
            connection.dialect.definition.tableSQL(TestTable).assertSQL {
                """
                CREATE TABLE IF NOT EXISTS t2 (id INT NOT NULL, lvalue INT NOT NULL, rvalue INT NOT NULL, CONSTRAINT PK_t2 PRIMARY KEY (id))
                CREATE INDEX IF NOT EXISTS one ON t2 (lvalue)
                CREATE INDEX IF NOT EXISTS two ON t2 (rvalue)
                """
            }
        }
    }
}

