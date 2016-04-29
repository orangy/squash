package org.jetbrains.squash

abstract class ColumnType() {
    override fun toString(): String = javaClass.simpleName
}

class CharacterColumnType() : ColumnType()
class IntegerColumnType() : ColumnType()
class LongColumnType() : ColumnType()
class DecimalColumnType(val scale: Int, val precision: Int) : ColumnType()
class EnumerationColumnType<T : Enum<T>>(val klass: Class<T>) : ColumnType()
class DateColumnType() : ColumnType()
class DateTimeColumnType() : ColumnType()
class StringColumnType(val length: Int = 65535, val collate: String? = null) : ColumnType()
class BinaryColumnType(val length: Int) : ColumnType()
class BlobColumnType() : ColumnType()
class BooleanColumnType() : ColumnType()
class UUIDColumnType() : ColumnType()

class ReferenceColumnType<T>(val column: Column<T>) : ColumnType()

