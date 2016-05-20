package org.jetbrains.squash.definition

/**
 * Represents a database column type
 */
abstract class ColumnType() {
    override fun toString(): String = "${javaClass.simpleName.removeSuffix("ColumnType")}"
}

object CharColumnType : ColumnType()
object IntColumnType : ColumnType()
object LongColumnType : ColumnType()
object DateColumnType : ColumnType()
object DateTimeColumnType : ColumnType()
object BlobColumnType : ColumnType()
object BooleanColumnType : ColumnType()
object UUIDColumnType : ColumnType()

class NullableColumnType(val columnType: ColumnType) : ColumnType() {
    override fun toString(): String = "${columnType.toString()}?"
}

class DecimalColumnType(val scale: Int, val precision: Int) : ColumnType() {
    override fun toString(): String = "Decimal($scale.$precision)"
}

class EnumColumnType(val klass: Class<*>) : ColumnType() {
    override fun toString(): String = "Enum<${klass.simpleName}>"
}

class StringColumnType(val length: Int = 65535, val collate: String? = null) : ColumnType() {
    override fun toString(): String = "String[$length]"
}

class BinaryColumnType(val length: Int) : ColumnType() {
    override fun toString(): String = "Binary[$length]"
}

class ReferenceColumnType<out V>(val column: Column<V>) : ColumnType() {
    override fun toString(): String = "Reference -> $column"
}

