package org.jetbrains.squash.dialects.postgres

import org.jetbrains.squash.definition.Json
import org.jetbrains.squash.drivers.JDBCDataConversion
import org.jetbrains.squash.drivers.JDBCResponseColumn
import org.postgresql.util.PGobject
import java.sql.Connection
import java.sql.ResultSet
import java.time.OffsetDateTime
import kotlin.reflect.KClass

class PgDataConversion : JDBCDataConversion() {
    override fun convertValueToDatabase(value: Any?, connection: Connection): Any? {
        return when {
            value is OffsetDateTime -> { value }
            Array<Int>::class.isInstance(value) -> {
                val typedArray = value as Array<Int>
                connection.createArrayOf("int4", typedArray)
            }
            Array<String>::class.isInstance(value) -> {
                val typedArray = value as Array<String>
                connection.createArrayOf("text", typedArray)
            }
            Json::class.isInstance(value) -> {
                val json = value as Json
                PGobject().apply {
                    this.type = "json"
                    this.value = json.json
                }
            }
            else -> super.convertValueToDatabase(value, connection)
        }
    }

    override fun convertValueFromDatabase(value: Any?, type: KClass<*>): Any? {
        return when (value) {
            type == java.time.OffsetDateTime::class && value is java.time.OffsetDateTime -> { value }
            type == kotlin.Array<Int>::class && value is java.sql.Array -> {
                val array = value as java.sql.Array
                array.array as Array<Int>
            }
            type == kotlin.Array<String>::class && value is java.sql.Array -> {
                val array = value as java.sql.Array
                array.array as Array<String>
            }
            type == Json::class && value is Json -> {
                val json = value as Json
                json.json
            }
            else -> super.convertValueFromDatabase(value, type)
        }
    }

    override fun fetch(resultSet: ResultSet, dbColumnIndex: Int, column: JDBCResponseColumn): Any? {
        return when (column.databaseType) {
            "_int4" -> resultSet.getArray(dbColumnIndex)?.array as Array<Int>?
            "_text" -> resultSet.getArray(dbColumnIndex)?.array as Array<String>?
            "timestamptz" -> resultSet.getObject(dbColumnIndex, OffsetDateTime::class.java)
            "json" -> Json(resultSet.getString(dbColumnIndex))
            else -> super.fetch(resultSet, dbColumnIndex, column)
        }
    }
}