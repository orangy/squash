package org.jetbrains.squash.benchmarks

import org.jetbrains.squash.dialects.h2.*

open class H2QueryBenchmark : QueryBenchmark() {
    override fun createTransaction() = H2Connection.createMemoryConnection().createTransaction()
}