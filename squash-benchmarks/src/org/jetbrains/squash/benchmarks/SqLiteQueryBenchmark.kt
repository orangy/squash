package org.jetbrains.squash.benchmarks

import org.jetbrains.squash.dialects.sqlite.*

open class SqLiteQueryBenchmark : QueryBenchmark() {
    // Keep one connection alive so that in-memory database doesn't get purged
    val dummy = SqLiteConnection.createMemoryConnection().createTransaction().jdbcTransaction
    override fun createConnection() = SqLiteConnection.createMemoryConnection()
}