package org.jetbrains.squash

import org.jetbrains.squash.expressions.*
import java.math.*
import java.sql.*
import java.time.*
import java.util.*


fun Column<Int>.autoIncrement(): Column<Int> = owner.replaceColumn(this, AutoIncrementColumn(this))

@JvmName("autoIncrementLong")
fun Column<Long>.autoIncrement(): Column<Long> = owner.replaceColumn(this, AutoIncrementColumn(this))

fun <T> Column<T>.primaryKey(): Column<T> = owner.replaceColumn(this, PrimaryKeyColumn(this))
fun <T> Column<T>.nullable(): Column<T?> = owner.replaceColumn(this, NullableColumn(this))
fun <T> Column<T>.default(value: T): Column<T> = owner.replaceColumn(this, DefaultValueColumn(this, value))

fun Name.referenceName(): String = when (this) {
    is Identifier -> id
    is QualifiedIdentifier<*> -> "${parent.referenceName()}_${identifier.referenceName()}"
    else -> error("Unknown Name '$this'")
}

fun <T> ColumnOwner.reference(column: Column<T>, name: String? = null): Column<T> {
    return createColumn(name ?: "${column.name.referenceName()}", ReferenceColumnType(column))
}

fun ColumnOwner.integer(name: String): Column<Int> {
    return createColumn(name, IntColumnType)
}

fun ColumnOwner.char(name: String): Column<Char> {
    return createColumn(name, CharColumnType)
}

inline fun <reified T : Enum<T>> ColumnOwner.enumeration(name: String): Column<T> {
    return createColumn(name, EnumColumnType(T::class.java))
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
