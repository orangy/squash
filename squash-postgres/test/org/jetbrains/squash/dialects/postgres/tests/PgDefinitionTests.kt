package org.jetbrains.squash.dialects.postgres.tests

import org.jetbrains.squash.tests.*

class PgDefinitionTests : DefinitionTests(), DatabaseTests by PgDatabaseTests() {
    override val sqlAllColumnTypes: String
        get() = "CREATE TABLE IF NOT EXISTS allColumnTypes (id SERIAL, name VARCHAR(42) NOT NULL, age INT NULL, CONSTRAINT PK_allColumnTypes PRIMARY KEY (name))"
}