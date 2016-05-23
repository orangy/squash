package org.jetbrains.squash.drivers

import org.jetbrains.squash.dialect.*
import java.sql.*
import kotlin.reflect.*

open class JDBCDataConversion {
    fun convertValueToDatabase(value: Any?): Any? {
        if (value == null)
            return null
        val type: KClass<*> = value.javaClass.kotlin
        when {
            type.java.isEnum -> return (value as Enum<*>).ordinal
            else -> return value
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
