package org.jetbrains.squash.definition

import org.jetbrains.squash.connection.*
import java.math.*
import java.time.*
import java.util.*
import kotlin.reflect.*

/**
 * Represents a database column type
 */
abstract class ColumnType(val runtimeType: KClass<*>) {
    override fun toString(): String = javaClass.simpleName.removeSuffix("ColumnType")
}

object IntColumnType : ColumnType(Int::class)
object LongColumnType : ColumnType(Long::class)
object DateColumnType : ColumnType(LocalDate::class)
object DateTimeColumnType : ColumnType(LocalDateTime::class)
object BlobColumnType : ColumnType(BinaryObject::class)
object BooleanColumnType : ColumnType(Boolean::class)
object UUIDColumnType : ColumnType(UUID::class)

class NullableColumnType(val columnType: ColumnType) : ColumnType(columnType.runtimeType) {
    override fun toString(): String = "$columnType?"
}

class DecimalColumnType(val scale: Int, val precision: Int) : ColumnType(BigDecimal::class) {
    override fun toString(): String = "Decimal($scale.$precision)"
}

class EnumColumnType(klass: KClass<*>) : ColumnType(klass) {
    override fun toString(): String = "Enum<${runtimeType.simpleName}>"
}

class StringColumnType(val length: Int = 65535, val collate: String? = null) : ColumnType(String::class) {
    override fun toString(): String = "String[$length]"
}

class CharColumnType(val length: Int) : ColumnType(String::class) {
    override fun toString(): String = "Char[$length]"
}

class BinaryColumnType(val length: Int) : ColumnType(ByteArray::class) {
    override fun toString(): String = "Binary[$length]"
}

class ReferenceColumnType(val type: ColumnType) : ColumnType(type.runtimeType) {
    override fun toString(): String = "Ref[$type]"
}

