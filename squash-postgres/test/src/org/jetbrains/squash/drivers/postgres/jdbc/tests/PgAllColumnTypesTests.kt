package org.jetbrains.squash.drivers.postgres.jdbc.tests

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.tests.*

class PgAllColumnTypesTests : AllColumnTypesTests(), DatabaseTests by PgDatabaseTests() {
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