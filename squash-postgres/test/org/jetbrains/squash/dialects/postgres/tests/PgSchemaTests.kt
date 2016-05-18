package org.jetbrains.squash.dialects.postgres.tests

import org.jetbrains.squash.tests.*

class PgSchemaTests : SchemaTests(), DatabaseTests by PgDatabaseTests() {
    override val sqlCitiesDDL: String
        get() = "CREATE TABLE IF NOT EXISTS Cities (id SERIAL, name VARCHAR(50) NOT NULL, CONSTRAINT PK_Cities PRIMARY KEY (id))"

    override val sqlSingleTableSchema = "CREATE TABLE TEST(ID SERIAL PRIMARY KEY, NAME VARCHAR(255))"

}