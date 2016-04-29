package org.jetbrains.squash

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

