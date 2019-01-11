package org.jetbrains.squash.drivers.postgres.tests

import ru.yandex.qatools.embed.postgresql.*
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres.*
import ru.yandex.qatools.embed.postgresql.config.*
import ru.yandex.qatools.embed.postgresql.distribution.*
import ru.yandex.qatools.embed.postgresql.util.*
import java.nio.file.*

data class EmbeddedPostgresConfig(val connectionString: String, val config: PostgresConfig)

val embeddedPostgresConfig by lazy {
    val config = EmbeddedPostgres.cachedRuntimeConfig(Paths.get("build/pg_embedded"))
    val embeddedPostgres = EmbeddedPostgres(Version.V9_6_8)
    val connectionString = embeddedPostgres.start(
        config,
        DEFAULT_HOST,
        SocketUtil.findFreePort(),
        DEFAULT_DB_NAME,
        DEFAULT_USER,
        DEFAULT_PASSWORD,
        listOf(
//            "-A", "md5",
            "-E", "SQL_ASCII",
            "--locale=C",
            "--lc-collate=C",
            "--lc-ctype=C"
        )
    )
    val postgresConfig = embeddedPostgres.config.get()
    println("Started Postgres: $postgresConfig")
    EmbeddedPostgresConfig(connectionString, postgresConfig)
}

