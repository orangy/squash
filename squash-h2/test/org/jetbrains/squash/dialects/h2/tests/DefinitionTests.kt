package org.jetbrains.squash.dialects.h2.tests

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.tests.*
import org.junit.*
import kotlin.test.*

@Suppress("unused")
class DefinitionTests {
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

    @Test fun unnamedTableWithQuotesSQL() {
        val TestTable = object : Table() {
            val id = integer("id").primaryKey()
            val name = varchar("name", length = 42)
        }

        withConnection {
            connection.dialect.definition.tableSQL(TestTable).assertSQL {
                "CREATE TABLE IF NOT EXISTS \"unnamedTableWithQuotesSQL\$TestTable$1\" (id INT NOT NULL, name VARCHAR(42) NOT NULL, CONSTRAINT \"PK_unnamedTableWithQuotesSQL\$TestTable$1\" PRIMARY KEY (id))"
            }
        }
    }

    @Test fun keywordNamedTableWithQuotesSQL() {
        val TestTable = object : Table("SELECT") {
            val id = integer("FROM").primaryKey()
            val name = varchar("JOIN", length = 42)
        }

        withConnection {
            connection.dialect.definition.tableSQL(TestTable).assertSQL {
                "CREATE TABLE IF NOT EXISTS \"SELECT\" (\"FROM\" INT NOT NULL, \"JOIN\" VARCHAR(42) NOT NULL, CONSTRAINT PK_SELECT PRIMARY KEY (\"FROM\"))"
            }
        }
    }

    @Test fun namedEmptyTableWithoutQuotesSQL() {
        val TestTable = object : Table("test_named_table") {
        }

        withConnection {
            connection.dialect.definition.tableSQL(TestTable).assertSQL {
                "CREATE TABLE IF NOT EXISTS test_named_table"
            }
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

        withConnection {
            connection.dialect.definition.tableSQL(TestTable).assertSQL {
                "CREATE TABLE IF NOT EXISTS test_table_with_different_column_types (id INT NOT NULL AUTO_INCREMENT, name VARCHAR(42) NOT NULL, age INT NULL, CONSTRAINT PK_test_table_with_different_column_types PRIMARY KEY (name))"
            }
        }
    }

    @Test fun columnsWithDefaults() {
        val TestTable = object : Table("t") {
            val s = varchar("s", 100).default("test")
            val l = long("l").default(42)
        }

        withConnection {
            connection.dialect.definition.tableSQL(TestTable).assertSQL {
                "CREATE TABLE IF NOT EXISTS t (s VARCHAR(100) NOT NULL DEFAULT ?, l BIGINT NOT NULL DEFAULT 42)"
            }
        }
    }

    @Test fun singleColumnIndex() {
        val TestTable = object : Table("t1") {
            val id = integer("id").primaryKey()
            val name = varchar("name", 255).index()
        }

        withConnection {
            connection.dialect.definition.tableSQL(TestTable).assertSQL {
                """
                CREATE TABLE IF NOT EXISTS t1 (id INT NOT NULL, name VARCHAR(255) NOT NULL, CONSTRAINT PK_t1 PRIMARY KEY (id))
                CREATE INDEX IX_t1_name ON t1 (name)
                """
            }
        }
    }

    @Test fun singleColumnUniqueIndex() {
        val TestTable = object : Table("t1") {
            val id = integer("id").primaryKey()
            val name = varchar("name", 255).uniqueIndex()
        }

        withConnection {
            connection.dialect.definition.tableSQL(TestTable).assertSQL {
                """
                CREATE TABLE IF NOT EXISTS t1 (id INT NOT NULL, name VARCHAR(255) NOT NULL, CONSTRAINT PK_t1 PRIMARY KEY (id))
                CREATE UNIQUE INDEX IX_t1_name ON t1 (name)
                """
            }
        }
    }

    @Test fun twoColumnIndex() {
        val TestTable = object : Table("t2") {
            val id = integer("id").primaryKey()
            val lvalue = integer("lvalue")
            val rvalue = integer("rvalue")

            init {
                index(lvalue, rvalue)
            }
        }

        withConnection {
            connection.dialect.definition.tableSQL(TestTable).assertSQL {
                """
                CREATE TABLE IF NOT EXISTS t2 (id INT NOT NULL, lvalue INT NOT NULL, rvalue INT NOT NULL, CONSTRAINT PK_t2 PRIMARY KEY (id))
                CREATE INDEX IX_t2_lvalue_rvalue ON t2 (lvalue, rvalue)
                """
            }
        }
    }

    @Test fun twoIndices() {
        val TestTable = object : Table("t2") {
            val id = integer("id").primaryKey()
            val lvalue = integer("lvalue").index("one")
            val rvalue = integer("rvalue").index("two")
        }

        withConnection {
            connection.dialect.definition.tableSQL(TestTable).assertSQL {
                """
                CREATE TABLE IF NOT EXISTS t2 (id INT NOT NULL, lvalue INT NOT NULL, rvalue INT NOT NULL, CONSTRAINT PK_t2 PRIMARY KEY (id))
                CREATE INDEX one ON t2 (lvalue)
                CREATE INDEX two ON t2 (rvalue)
                """
            }
        }
    }
}

