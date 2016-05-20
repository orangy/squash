package org.jetbrains.squash.tests

import org.jetbrains.squash.connection.*
import org.jetbrains.squash.query.*
import org.jetbrains.squash.statements.*
import org.jetbrains.squash.tests.data.*
import org.junit.*
import java.math.*
import java.time.*
import java.util.*

@Suppress("unused")
abstract class AllColumnTypesTests : DatabaseTests {
    protected open val allColumnsTableSQL: String get() = "CREATE TABLE IF NOT EXISTS AllColumnTypes (" +
            "id $idColumnType, " +
            "\"varchar\" VARCHAR(42) NOT NULL, " +
            "\"char\" CHAR NULL, " +
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
            val row = query(AllColumnTypes).execute().single()
        }
    }


    private fun Transaction.insertData() {
        insertInto(AllColumnTypes).values {
            it[varchar] = "varchar"
            it[char] = 'c'
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