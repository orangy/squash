package org.jetbrains.squash.drivers.postgres.cio

import io.ktor.experimental.client.postgre.*
import org.jetbrains.squash.dialects.postgres.*
import org.jetbrains.squash.drivers.cio.*
import java.net.*

class PgCioConnection(client: PostgreClient) : CIOConnection(client, PgDialect) {
    companion object {
        fun create(host: String, port: Int, database: String, username: String, password: String): PgCioConnection {
            return PgCioConnection(
                PostgreClient(InetSocketAddress(host, port), database = database, user = username, password = password)
            )
        }

        fun create(connectionString: String): PgCioConnection {
            TODO("parse JDBC connection string")
            val url = URL(connectionString)
            return PgCioConnection(
                PostgreClient(
                    InetSocketAddress.createUnresolved(url.host, url.port)
                )
            )
        }
    }
}