package org.jetbrains.squash.benchmarks

import org.jetbrains.squash.dialects.postgres.*
import org.jetbrains.squash.drivers.jdbc.postgres.*
import org.jetbrains.squash.drivers.postgres.jdbc.*
import org.openjdk.jmh.annotations.*
import ru.yandex.qatools.embed.postgresql.*
import java.nio.file.*


open class PgQueryBenchmark : QueryBenchmark() {
    private lateinit var pg: EmbeddedPostgres
    private lateinit var pgUrl: String

    @Setup
    fun startPostgres() {
        pg = EmbeddedPostgres()
        pgUrl = pg.start(EmbeddedPostgres.cachedRuntimeConfig(Paths.get("build/pg_embedded")))
    }

    @TearDown
    fun stopPostgres() {
        pg.stop()
    }

    override fun createConnection() = PgConnection.create(pgUrl)
}