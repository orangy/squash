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

object BenchmarkTable : TableDefinition() {
    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", 20)
    val value = integer("value").index()
}

interface Data {
    val name: String
    val value: Int
}

@State(Scope.Benchmark)
abstract class QueryBenchmark {
    private val rows = 100000
    lateinit private var connection: DatabaseConnection

    abstract fun createConnection(): DatabaseConnection

    @Setup
    fun setup() {
        connection = createConnection()

        withTransaction {
            connection.monitor.before {
                //println(it)
            }
            databaseSchema().create(listOf(BenchmarkTable))
            repeat(rows) { seq ->
                insertInto(BenchmarkTable).values {
                    it[name] = "$seq-value"
                    it[value] = seq
                }.execute()
            }
        }
    }


    @TearDown
    fun teardown() {
        connection.close()
    }

    fun <R> withTransaction(body: Transaction.() -> R): R {
        return connection.createTransaction().use {
            body(it)
        }
    }

    fun <R> withJDBCTransaction(body: JDBCTransaction.() -> R): R {
        return connection.createTransaction().use {
            body(it as JDBCTransaction)
        }
    }


    @Benchmark
    fun iterateJdbc() = withJDBCTransaction {
        val resultSet = jdbcTransaction.prepareStatement("SELECT * FROM Benchmark").executeQuery()
        var sum = 0
        val index = resultSet.findColumn("value")
        while (resultSet.next()) {
            sum += resultSet.getInt(index)
        }
        sum
    }

    @Benchmark
    fun iterateJdbcObject() = withJDBCTransaction {
        val resultSet = jdbcTransaction.prepareStatement("SELECT * FROM Benchmark").executeQuery()
        var sum = 0
        val index = resultSet.findColumn("value")
        while (resultSet.next()) {
            sum += resultSet.getObject(index) as Int
        }
        sum
    }

    @Benchmark
    fun iterateJdbcName() = withJDBCTransaction {
        val resultSet = jdbcTransaction.prepareStatement("SELECT * FROM Benchmark").executeQuery()
        var sum = 0
        while (resultSet.next()) {
            sum += resultSet.getInt("value")
        }
        sum
    }

    @Benchmark
    fun iterateQuery() = withTransaction {
        val query = from(BenchmarkTable).select(BenchmarkTable.name, BenchmarkTable.value)
        val response = query.execute()
        response.sumBy { it.columnValue(BenchmarkTable.value) }
    }

    @Benchmark
    fun iterateQueryWhere() = withTransaction {
        val query = from(BenchmarkTable).select(BenchmarkTable.name, BenchmarkTable.value)
            .where { BenchmarkTable.value gt (rows / 2) }
        val response = query.execute()
        response.sumBy { it.columnValue(BenchmarkTable.value) }
    }

    @Benchmark
    fun iterateMapping() = withTransaction {
        val query = from(BenchmarkTable).select(BenchmarkTable.name, BenchmarkTable.value).bind<Data>(BenchmarkTable)
        val response = query.execute()
        response.sumBy { it.value }
    }
}

