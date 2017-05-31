package org.jetbrains.squash.dialects.mysql.tests

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.tests.*

class MySqlAllColumnTypesTests : AllColumnTypesTests(), DatabaseTests by MySqlDatabaseTests() {
    override val allColumnsTableSQL: String get() = "CREATE TABLE IF NOT EXISTS AllColumnTypes (" +
            "id ${getIdColumnType(IntColumnType)}, " +
            "${quote}varchar${quote} VARCHAR(42) NOT NULL, " +
            "${quote}char${quote} CHAR NOT NULL, " +
            "enum INT NOT NULL, " +
            "${quote}decimal${quote} DECIMAL(5, 2) NOT NULL, " +
            "${quote}long${quote} BIGINT NOT NULL, " +
            "${quote}date${quote} DATE NOT NULL, " +
            "bool BOOLEAN NOT NULL, " +
            "datetime DATETIME NOT NULL, " +
            "text TEXT NOT NULL, " +
            "${quote}binary${quote} VARBINARY(128) NOT NULL, " +
            "${quote}blob${quote} $blobType NOT NULL, " +
            "uuid BINARY(16) NOT NULL, " +
            "CONSTRAINT PK_AllColumnTypes PRIMARY KEY (id))"

}