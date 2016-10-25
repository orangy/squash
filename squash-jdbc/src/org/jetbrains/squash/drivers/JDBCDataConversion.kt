package org.jetbrains.squash.drivers

import org.jetbrains.squash.connection.*
import java.lang.Boolean as JavaLangBoolean
import java.lang.Byte as JavaLangByte
import java.lang.Character as JavaLangCharacter
import java.lang.Double as JavaLangDouble
import java.lang.Float as JavaLangFloat
import java.lang.Integer as JavaLangInteger
import java.lang.Long as JavaLangLong
import java.lang.Short as JavaLangShort
import java.sql.*
import java.time.*
import kotlin.jvm.internal.*
import kotlin.reflect.*

open class JDBCDataConversion {
    open fun convertValueToDatabase(value: Any?): Any? {
        if (value == null)
            return null
        val type: KClass<*> = value.javaClass.kotlin
        return when {
            type.java.isEnum -> (value as Enum<*>).ordinal
            value is LocalDate -> Date.valueOf(value)
            value is LocalTime -> Time.valueOf(value)
            value is LocalDateTime -> Timestamp.valueOf(value)
            value is JDBCBinaryObject -> value.bytes
            else -> value
        }
    }

    private val <T : Any> KClass<T>.javaObjectType: Class<T>
        get() {
            val thisJClass = (this as ClassBasedDeclarationContainer).jClass
            if (!thisJClass.isPrimitive) return thisJClass as Class<T>

            return when (thisJClass.name) {
                "boolean" -> JavaLangBoolean::class.java
                "char"    -> JavaLangCharacter::class.java
                "byte"    -> JavaLangByte::class.java
                "short"   -> JavaLangShort::class.java
                "int"     -> JavaLangInteger::class.java
                "float"   -> JavaLangFloat::class.java
                "long"    -> JavaLangLong::class.java
                "double"  -> JavaLangDouble::class.java
                else -> thisJClass
            } as Class<T>
        }


    open fun convertValueFromDatabase(value: Any?, type: KClass<*>): Any? {
        return when {
            value == null -> null
            value is Int && type.java.isEnum -> type.java.enumConstants[value]
            value is Clob -> value.characterStream.readText()
            value is Timestamp -> value.toLocalDateTime()
            value is Date -> value.toLocalDate()
            value is Time -> value.toLocalTime()
            value is Blob -> JDBCBinaryObject(value.getBytes(1, value.length().toInt()))
            value is ByteArray && type == BinaryObject::class -> JDBCBinaryObject(value)
            type.javaObjectType.isInstance(value) -> value
            value is Long && type.javaObjectType == Int::class.javaObjectType -> value.toInt()
            value is Int && type.javaObjectType == Long::class.javaObjectType -> value.toLong()
            else -> error("Cannot convert value of type `${value.javaClass}` to type `$type`")
        }
    }
}
