package org.jetbrains.squash.benchmarks

import org.jetbrains.squash.dialects.h2.*

open class H2QueryBenchmark : QueryBenchmark() {
    override fun createConnection() = H2Connection.createMemoryConnection()
}