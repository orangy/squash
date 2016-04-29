package org.jetbrains.squash

abstract class ColumnType() {
    override fun toString(): String = "${javaClass.simpleName.removeSuffix("ColumnType")}"
}

class CharColumnType() : ColumnType()
class IntColumnType() : ColumnType()
class LongColumnType() : ColumnType()
class DateColumnType() : ColumnType()
class DateTimeColumnType() : ColumnType()
class BlobColumnType() : ColumnType()
class BooleanColumnType() : ColumnType()
class UUIDColumnType() : ColumnType()

class DecimalColumnType(val scale: Int, val precision: Int) : ColumnType() {
    override fun toString(): String = "Decimal($scale.$precision)"
}

class EnumColumnType<T : Enum<T>>(val klass: Class<T>) : ColumnType() {
    override fun toString(): String = "Enum<${klass.simpleName}>"
}

class StringColumnType(val length: Int = 65535, val collate: String? = null) : ColumnType() {
    override fun toString(): String = "String[$length]"
}

class BinaryColumnType(val length: Int) : ColumnType() {
    override fun toString(): String = "Binary[$length]"
}

class ReferenceColumnType<T>(val column: Column<T>) : ColumnType() {
    override fun toString(): String = "Reference -> $column"
}

