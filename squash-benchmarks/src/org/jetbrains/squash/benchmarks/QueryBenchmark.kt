package org.jetbrains.squash.benchmarks

import org.jetbrains.squash.connection.*
import org.jetbrains.squash.definition.*
import org.jetbrains.squash.drivers.*
import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.graph.*
import org.jetbrains.squash.query.*
import org.jetbrains.squash.results.*
import org.jetbrains.squash.statements.*
import org.openjdk.jmh.annotations.*

object LoadTable : TableDefinition() {
    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", 20)
    val value = integer("value").index()
}

interface Load {
    val name: String
    val value: Int
}

@State(Scope.Benchmark)
abstract class QueryBenchmark {
    private val rows = 100000
    lateinit private var transaction: Transaction

    abstract fun createTransaction(): Transaction

    @Setup
    fun setup() {
        transaction = createTransaction().apply {
            connection.monitor.before {
                //println(it)
            }
            databaseSchema().create(listOf(LoadTable))
            repeat(rows) { seq ->
                insertInto(LoadTable).values {
                    it[name] = "$seq-value"
                    it[value] = seq
                }.execute()
            }
        }
    }


    @TearDown
    fun teardown() {
        transaction.close()
    }

    @Benchmark
    fun iterateJdbc() = with(transaction as JDBCTransaction) {
        val resultSet = jdbcTransaction.prepareStatement("SELECT * FROM Load").executeQuery()
        var sum = 0
        val index = resultSet.findColumn("value")
        while (resultSet.next()) {
            sum += resultSet.getInt(index)
        }
        sum
    }

    @Benchmark
    fun iterateJdbcObject() = with(transaction as JDBCTransaction) {
        val resultSet = jdbcTransaction.prepareStatement("SELECT * FROM Load").executeQuery()
        var sum = 0
        val index = resultSet.findColumn("value")
        while (resultSet.next()) {
            sum += resultSet.getObject(index) as Int
        }
        sum
    }

    @Benchmark
    fun iterateJdbcName() = with(transaction as JDBCTransaction) {
        val resultSet = jdbcTransaction.prepareStatement("SELECT * FROM Load").executeQuery()
        var sum = 0
        while (resultSet.next()) {
            sum += resultSet.getInt("value")
        }
        sum
    }

    @Benchmark
    fun iterateQuery() = with(transaction) {
        query(LoadTable).select(LoadTable.name, LoadTable.value).execute().sumBy { it.columnValue(LoadTable.value) }
    }

    @Benchmark
    fun iterateQueryWhere() = with(transaction) {
        query(LoadTable).select(LoadTable.name, LoadTable.value).where { LoadTable.value gt (rows / 2) }.execute().sumBy { it.columnValue(LoadTable.value) }
    }

    @Benchmark
    fun iterateMapping() = with(transaction) {
        query(LoadTable).select(LoadTable.name, LoadTable.value).bind<Load>(LoadTable)
                .execute().sumBy { it.value }
    }
}

