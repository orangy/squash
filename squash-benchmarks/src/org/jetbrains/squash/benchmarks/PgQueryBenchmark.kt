package org.jetbrains.squash.benchmarks

import com.opentable.db.postgres.embedded.*
import org.jetbrains.squash.dialects.postgres.*
import org.jetbrains.squash.dialects.sqlite.*
import org.openjdk.jmh.annotations.*

open class PgQueryBenchmark : QueryBenchmark() {
    lateinit var pg : EmbeddedPostgres

    @Setup
    fun startPostgres() {
        pg = EmbeddedPostgres.start()
    }

    override fun createTransaction() = PgConnection.create("localhost:${pg.port}/", "postgres").createTransaction()
}