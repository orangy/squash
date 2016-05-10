package org.jetbrains.squash.definition

import org.jetbrains.squash.*
import java.math.*
import java.sql.*
import java.time.*
import java.util.*

fun Column<Int>.autoIncrement(): Column<Int> = owner.replaceColumn(this, AutoIncrementColumn(this))

@JvmName("autoIncrementLong")
fun Column<Long>.autoIncrement(): Column<Long> = owner.replaceColumn(this, AutoIncrementColumn(this))

fun <V> Column<V>.primaryKey(): Column<V> = owner.replaceColumn(this, PrimaryKeyColumn(this))
fun <V> Column<V>.nullable(): Column<V?> = owner.replaceColumn(this, NullableColumn(this))
fun <V> Column<V>.default(value: V): Column<V> = owner.replaceColumn(this, DefaultValueColumn(this, value))

fun Name.referenceName(): String = when (this) {
    is Identifier -> id
    is QualifiedIdentifier<*> -> "${parent.referenceName()}_${identifier.referenceName()}"
    else -> error("Unknown Name '$this'")
}

fun <V> ColumnOwner.reference(column: Column<V>, name: String? = null): Column<V> {
    return createColumn(name ?: "${column.name.referenceName()}", ReferenceColumnType(column))
}

fun ColumnOwner.integer(name: String): Column<Int> {
    return createColumn(name, IntColumnType)
}

fun ColumnOwner.char(name: String): Column<Char> {
    return createColumn(name, CharColumnType)
}

inline fun <reified V : Enum<V>> ColumnOwner.enumeration(name: String): Column<V> {
    return createColumn(name, EnumColumnType(V::class.java))
}

fun ColumnOwner.decimal(name: String, scale: Int, precision: Int): Column<BigDecimal> {
    return createColumn(name, DecimalColumnType(scale, precision))
}

fun ColumnOwner.long(name: String): Column<Long> {
    return createColumn(name, LongColumnType)
}

fun ColumnOwner.date(name: String): Column<LocalDate> {
    return createColumn(name, DateColumnType)
}

fun ColumnOwner.bool(name: String): Column<Boolean> {
    return createColumn(name, BooleanColumnType)
}

fun ColumnOwner.datetime(name: String): Column<LocalDateTime> {
    return createColumn(name, DateTimeColumnType)
}

fun ColumnOwner.blob(name: String): Column<Blob> { // TODO: It's java.sql, avoid
    return createColumn(name, BlobColumnType)
}

fun ColumnOwner.text(name: String): Column<String> {
    return createColumn(name, StringColumnType())
}

fun ColumnOwner.binary(name: String, length: Int): Column<ByteArray> {
    return createColumn(name, BinaryColumnType(length))
}

fun ColumnOwner.uuid(name: String): Column<UUID> {
    return createColumn(name, UUIDColumnType)
}

fun ColumnOwner.varchar(name: String, length: Int, collate: String? = null): Column<String> {
    return createColumn(name, StringColumnType(length, collate))
}
