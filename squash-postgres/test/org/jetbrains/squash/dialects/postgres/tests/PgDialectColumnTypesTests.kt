package org.jetbrains.squash.dialects.postgres.tests

import org.jetbrains.squash.connection.Transaction
import org.jetbrains.squash.definition.IntColumnType
import org.jetbrains.squash.definition.Json
import org.jetbrains.squash.expressions.contains
import org.jetbrains.squash.expressions.containsAny
import org.jetbrains.squash.query.from
import org.jetbrains.squash.query.where
import org.jetbrains.squash.results.ResultRow
import org.jetbrains.squash.results.get
import org.jetbrains.squash.statements.insertInto
import org.jetbrains.squash.statements.values
import org.jetbrains.squash.tests.DatabaseTests
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PgDialectColumnTypesTests: DatabaseTests by PgDatabaseTests() {
    private val dialectColumnsTableSQL: String get() = "CREATE TABLE IF NOT EXISTS PgDialectColumnTypes (" +
            "id ${getIdColumnType(IntColumnType)}, " +
            "offsetdatetime TIMESTAMP WITH TIME ZONE NOT NULL, " +
            "intarray INT[] NOT NULL, " +
            "nullable_intarray INT[] NULL, " +
            "textarray TEXT[] NOT NULL, " +
            "nullable_textarray TEXT[] NULL, " +
            "notnull_jsonb JSONB NOT NULL, " +
            "nullable_jsonb JSONB NULL, " +
            "CONSTRAINT PK_PgDialectColumnTypes PRIMARY KEY (id))"

    @Test fun sql() {
        withTransaction {
            connection.dialect.definition.tableSQL(PgDialectColumnTypes).assertSQL { dialectColumnsTableSQL }
        }
    }

    @Test fun insert() {
        withTables(PgDialectColumnTypes) {
            insertData()
        }
    }

    private val offsetDate = OffsetDateTime.of(1976, 11, 24, 12, 1, 1, 0, ZoneOffset.ofHours(-6))
    private val arrayOfInt = arrayOf(1, 2, 3, 4)
    private val arrayOfString = arrayOf("see", "spot", "run")
    private val jsonb = Json("{}")

    @Test fun query() {
        withTables(PgDialectColumnTypes) {
            insertData()

            fun checkRow(row: ResultRow) {
                assertEquals(OffsetDateTime::class.java, row[PgDialectColumnTypes.offsetdatetime].javaClass)
                assertEquals(offsetDate.toEpochSecond(), row[PgDialectColumnTypes.offsetdatetime].toEpochSecond())

                assertEquals(Array<Int>::class.java, row[PgDialectColumnTypes.notnullIntarray].javaClass)
                assertTrue { Arrays.equals(arrayOfInt, row[PgDialectColumnTypes.notnullIntarray]) }

                assertEquals(Array<String>::class.java, row[PgDialectColumnTypes.notnullTextarray].javaClass)
                assertTrue { Arrays.equals(arrayOfString, row[PgDialectColumnTypes.notnullTextarray]) }
            }

            val containsRow = from(PgDialectColumnTypes).where { PgDialectColumnTypes.notnullIntarray contains  arrayOfInt.take(2) }.execute().single()
            checkRow(containsRow)

            val containsAnyRow = from(PgDialectColumnTypes).where { PgDialectColumnTypes.notnullTextarray containsAny listOf(arrayOfString.first(), "other")}.execute().single()
            checkRow(containsAnyRow)

            val noRow = from(PgDialectColumnTypes).where { PgDialectColumnTypes.notnullIntarray contains listOf(9) }.execute().singleOrNull()
            assertNull(noRow)
        }
    }

    private fun Transaction.insertData() {
        insertInto(PgDialectColumnTypes).values {
            it[offsetdatetime] = offsetDate
            it[notnullIntarray] = arrayOfInt
            it[nullableIntarray] = null
            it[notnullTextarray] = arrayOfString
            it[nullableTextarray] = arrayOfString
            it[notnullJsonb] = jsonb
            it[nullableJsonb] = null
        }.execute()
    }
}