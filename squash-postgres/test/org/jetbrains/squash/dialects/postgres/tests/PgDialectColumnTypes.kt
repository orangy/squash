package org.jetbrains.squash.dialects.postgres.tests

import org.jetbrains.squash.definition.*

object PgDialectColumnTypes : TableDefinition() {
    val id = integer("id").autoIncrement().primaryKey()
    val offsetdatetime = offsetDatetime("offsetdatetime")
    val notnullIntarray = intArray("intarray")
    val nullableIntarray = intArray("nullable_intarray").nullable()
    val notnullTextarray = textArray("textarray")
    val nullableTextarray = textArray("nullable_textarray").nullable()
    val notnullJsonb = jsonb("notnull_jsonb")
    val nullableJsonb = jsonb("nullable_jsonb").nullable()
}
