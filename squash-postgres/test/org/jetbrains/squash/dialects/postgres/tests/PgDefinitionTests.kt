package org.jetbrains.squash.dialects.postgres.tests

import org.jetbrains.squash.tests.*

class PgDefinitionTests : DefinitionTests(), DatabaseTests by PgDatabaseTests() {
    override val allColumnsTableSQL: String
        get() = "CREATE TABLE IF NOT EXISTS AllColumnTypes (" +
                "id $idColumnType, " +
                "\"varchar\" VARCHAR(42) NOT NULL, " +
                "\"char\" CHAR NULL, " +
                "enum INT NOT NULL, " +
                "\"decimal\" DECIMAL(5, 2) NOT NULL, " +
                "long BIGINT NOT NULL, " +
                "\"date\" DATE NOT NULL, " +
                "bool BOOLEAN NOT NULL, " +
                "datetime TIMESTAMP NOT NULL, " +
                "text TEXT NOT NULL, " +
                "\"binary\" bytea NOT NULL, " +
                "uuid bytea NOT NULL, " +
                "CONSTRAINT PK_AllColumnTypes PRIMARY KEY (\"varchar\"))"

}