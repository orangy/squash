package org.jetbrains.squash.benchmarks

import com.opentable.db.postgres.embedded.*
import org.jetbrains.squash.dialects.postgres.*
import org.openjdk.jmh.annotations.*

open class PgQueryBenchmark : QueryBenchmark() {
    lateinit var pg : EmbeddedPostgres

    @Setup
    fun startPostgres() {
        pg = EmbeddedPostgres.start()
    }

    @TearDown
    fun stopPostgres() {
        pg.close()
    }

    override fun createConnection() = PgConnection.create("localhost:${pg.port}/", "postgres")
}