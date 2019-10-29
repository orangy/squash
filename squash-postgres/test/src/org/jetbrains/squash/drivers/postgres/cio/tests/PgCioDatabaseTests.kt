package org.jetbrains.squash.drivers.postgres.cio.tests

/*
import org.jetbrains.squash.definition.*
import org.jetbrains.squash.drivers.postgres.cio.*
import org.jetbrains.squash.drivers.postgres.jdbc.tests.*
import org.jetbrains.squash.drivers.postgres.tests.*
import org.jetbrains.squash.tests.*

class PgCioDatabaseTests : PgDatabaseTests() {
    override fun createConnection(): PgCioConnection {
        val config = embeddedPostgresConfig.config
        val credentials = config.credentials()
        val net = config.net()
        val storage = config.storage()
        return PgCioConnection.create(
            net.host(),
            net.port(),
            storage.dbName(),
            credentials.username(),
            credentials.password()
        )
    }
}

class PgCioDefinitionTests : DefinitionTests(), DatabaseTests by PgCioDatabaseTests()
class PgCioQueryTests : QueryTests(), DatabaseTests by PgCioDatabaseTests()
class PgCioAllColumnTypesTests : AllColumnTypesTests(), DatabaseTests by PgCioDatabaseTests() {
    override val allColumnsTableSQL: String
        get() = "CREATE TABLE IF NOT EXISTS AllColumnTypes (" +
                "id ${getIdColumnType(IntColumnType)}, " +
                "\"varchar\" VARCHAR(42) NOT NULL, " +
                "\"char\" CHAR NOT NULL, " +
                "enum INT NOT NULL, " +
                "\"decimal\" DECIMAL(5, 2) NOT NULL, " +
                "long BIGINT NOT NULL, " +
                "\"date\" DATE NOT NULL, " +
                "bool BOOLEAN NOT NULL, " +
                "datetime TIMESTAMP NOT NULL, " +
                "text TEXT NOT NULL, " +
                "\"binary\" BYTEA NOT NULL, " +
                "\"blob\" BYTEA NOT NULL, " +
                "uuid UUID NOT NULL, " +
                "CONSTRAINT PK_AllColumnTypes PRIMARY KEY (id))"
}
*/