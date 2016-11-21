package org.jetbrains.squash.benchmarks

import org.jetbrains.squash.dialects.sqlite.*

open class SqLiteQueryBenchmark : QueryBenchmark() {
    override fun createTransaction() = SqLiteConnection.createMemoryConnection().createTransaction()
}