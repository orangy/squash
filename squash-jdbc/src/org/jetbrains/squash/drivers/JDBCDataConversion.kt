package org.jetbrains.squash.drivers

import java.sql.*
import java.time.*
import kotlin.reflect.*

open class JDBCDataConversion {
    fun convertValueToDatabase(value: Any?): Any? {
        if (value == null)
            return null
        val type: KClass<*> = value.javaClass.kotlin
        return when {
            type.java.isEnum -> (value as Enum<*>).ordinal
            value is LocalDate -> Date.valueOf(value)
            value is LocalTime -> Time.valueOf(value)
            value is LocalDateTime -> Timestamp.valueOf(value)
            else ->  value
        }
    }

    fun convertValueFromDatabase(value: Any?, type: KClass<*>): Any? {
        when {
            value is Int && type.java.isEnum -> return type.java.enumConstants[value]
            value is Clob -> return value.characterStream.readText()
            value is Timestamp -> return value.toLocalDateTime()
            value is Date -> return value.toLocalDate()
            value is Time -> return value.toLocalTime()
            else -> return value
        }
    }
}
