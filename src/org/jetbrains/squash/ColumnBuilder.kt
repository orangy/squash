package org.jetbrains.squash

import java.math.*
import java.sql.*
import java.time.*
import java.util.*


fun Column<Int>.autoIncrement(): Column<Int> = table.replaceColumn(this, AutoIncrementColumn(this))

@JvmName("autoIncrementLong")
fun Column<Long>.autoIncrement(): Column<Long> = table.replaceColumn(this, AutoIncrementColumn(this))

fun <T> Column<T>.primaryKey(): Column<T> = table.replaceColumn(this, PrimaryKeyColumn(this))
fun <T> Column<T>.nullable(): Column<T> = table.replaceColumn(this, NullableColumn(this))
fun <T> Column<T>.default(value: T): Column<T> = table.replaceColumn(this, DefaultValueColumn(this, value))

fun <T> Table.reference(column: Column<T>, name: String? = null): Column<T> {
    return createColumn(name ?: "${column.table.tableName}_${column.name}", ReferenceColumnType(column))
}

fun Table.integer(name: String): Column<Int> {
    return createColumn(name, IntColumnType)
}

fun Table.char(name: String): Column<Char> {
    return createColumn(name, CharColumnType)
}

inline fun <reified T : Enum<T>> Table.enumeration(name: String): Column<T> {
    return createColumn(name, EnumColumnType(T::class.java))
}

fun Table.decimal(name: String, scale: Int, precision: Int): Column<BigDecimal> {
    return createColumn(name, DecimalColumnType(scale, precision))
}

fun Table.long(name: String): Column<Long> {
    return createColumn(name, LongColumnType)
}

fun Table.date(name: String): Column<LocalDate> {
    return createColumn(name, DateColumnType)
}

fun Table.bool(name: String): Column<Boolean> {
    return createColumn(name, BooleanColumnType)
}

fun Table.datetime(name: String): Column<LocalDateTime> {
    return createColumn(name, DateTimeColumnType)
}

fun Table.blob(name: String): Column<Blob> { // TODO: It's java.sql, avoid
    return createColumn(name, BlobColumnType)
}

fun Table.text(name: String): Column<String> {
    return createColumn(name, StringColumnType())
}

fun Table.binary(name: String, length: Int): Column<ByteArray> {
    return createColumn(name, BinaryColumnType(length))
}

fun Table.uuid(name: String): Column<UUID> {
    return createColumn(name, UUIDColumnType)
}

fun Table.varchar(name: String, length: Int, collate: String? = null): Column<String> {
    return createColumn(name, StringColumnType(length, collate))
}
