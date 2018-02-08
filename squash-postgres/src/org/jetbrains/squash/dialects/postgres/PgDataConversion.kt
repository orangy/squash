package org.jetbrains.squash.dialects.postgres

import org.jetbrains.squash.drivers.*
import java.time.OffsetDateTime
import kotlin.reflect.KClass

class PgDataConversion : JDBCDataConversion() {
    override fun convertValueToDatabase(value: Any?): Any? {
        return when (value) {
            value is OffsetDateTime -> value
            else -> super.convertValueToDatabase(value)
        }
    }

    override fun convertValueFromDatabase(value: Any?, type: KClass<*>): Any? {
        return when (value) {
            value is OffsetDateTime -> value
            else -> super.convertValueFromDatabase(value, type)
        }
    }
}