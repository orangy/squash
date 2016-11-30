package org.jetbrains.squash.tests

import org.jetbrains.squash.connection.*
import org.jetbrains.squash.query.*
import org.jetbrains.squash.results.*
import org.jetbrains.squash.statements.*
import org.jetbrains.squash.tests.data.*
import org.junit.*
import java.math.*
import java.time.*
import java.util.*
import kotlin.test.*

@Suppress("unused")
abstract class AllColumnTypesTests : DatabaseTests {
    protected open val allColumnsTableSQL: String get() = "CREATE TABLE IF NOT EXISTS AllColumnTypes (" +
            "id $idColumnType, " +
            "\"varchar\" VARCHAR(42) NOT NULL, " +
            "\"char\" CHAR NOT NULL, " +
            "enum INT NOT NULL, " +
            "\"decimal\" DECIMAL(5, 2) NOT NULL, " +
            "long BIGINT NOT NULL, " +
            "\"date\" DATE NOT NULL, " +
            "bool BOOLEAN NOT NULL, " +
            "datetime DATETIME NOT NULL, " +
            "text TEXT NOT NULL, " +
            "\"binary\" VARBINARY(128) NOT NULL, " +
            "uuid UUID NOT NULL, " +
            "CONSTRAINT PK_AllColumnTypes PRIMARY KEY (\"varchar\"))"

    @Test fun sql() {
        withTransaction {
            connection.dialect.definition.tableSQL(AllColumnTypes).assertSQL { allColumnsTableSQL }
        }
    }

    @Test fun insert() {
        withTables(AllColumnTypes) {
            insertData()
        }
    }

    @Test fun query() {
        withTables(AllColumnTypes) {
            insertData()
            val row = from(AllColumnTypes).execute().single()

            assertEquals(String::class.java, row[AllColumnTypes.varchar].javaClass)
            assertEquals("varchar", row[AllColumnTypes.varchar])

            assertEquals(Long::class.java, row[AllColumnTypes.long].javaClass)
            assertEquals(222L, row[AllColumnTypes.long])

            assertEquals(Boolean::class.java, row[AllColumnTypes.bool].javaClass)
            assertEquals(true, row[AllColumnTypes.bool])

            assertEquals(ByteArray::class.java, row[AllColumnTypes.binary].javaClass)
            assertEquals(byteArrayOf(1, 2, 3).toList(), row[AllColumnTypes.binary].toList())

            assertEquals(UUID::class.java, row[AllColumnTypes.uuid].javaClass)
            assertEquals(UUID.fromString("7cb64fe4-4938-4e88-8d94-17e929d40c99"), row[AllColumnTypes.uuid])

            assertEquals(String::class.java, row[AllColumnTypes.text].javaClass)
            assertEquals("Lorem Ipsum", row[AllColumnTypes.text])

            assertEquals(BigDecimal::class.java, row[AllColumnTypes.decimal].javaClass)
            assertEquals(BigDecimal.ONE.setScale(2), row[AllColumnTypes.decimal])

            assertEquals(String::class.java, row[AllColumnTypes.char].javaClass)
            assertEquals("c", row[AllColumnTypes.char])

            assertEquals(LocalDateTime::class.java, row[AllColumnTypes.datetime].javaClass)
            assertEquals(LocalDateTime.of(LocalDate.of(1976, 11, 24), LocalTime.of(8, 22)), row[AllColumnTypes.datetime])

            assertEquals(LocalDate::class.java, row[AllColumnTypes.date].javaClass)
            assertEquals(LocalDate.of(1976, 11, 24), row[AllColumnTypes.date])

            assertEquals(E::class.java, row[AllColumnTypes.enum].javaClass)
            assertEquals(E.ONE, row[AllColumnTypes.enum])

        }
    }

    private fun Transaction.insertData() {
        insertInto(AllColumnTypes).values {
            it[varchar] = "varchar"
            it[char] = "c"
            it[enum] = E.ONE
            it[decimal] = BigDecimal.ONE
            it[long] = 222L
            it[date] = LocalDate.of(1976, 11, 24)
            it[bool] = true
            it[datetime] = LocalDateTime.of(LocalDate.of(1976, 11, 24), LocalTime.of(8, 22))
            it[text] = "Lorem Ipsum"
            it[binary] = byteArrayOf(1, 2, 3)
            it[uuid] = UUID.fromString("7cb64fe4-4938-4e88-8d94-17e929d40c99")
        }.execute()
    }
}