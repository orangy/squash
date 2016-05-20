@file:JvmName("Columns")

package org.jetbrains.squash.definition

import org.jetbrains.squash.*
import java.math.*
import java.sql.*
import java.time.*
import java.util.*

fun Column<Int>.autoIncrement(): Column<Int> = table.replaceColumn(this, AutoIncrementColumn(this))

@JvmName("autoIncrementLong")
fun Column<Long>.autoIncrement(): Column<Long> = table.replaceColumn(this, AutoIncrementColumn(this))

fun <V> Column<V>.nullable(): Column<V?> = table.replaceColumn(this, NullableColumn(this))
fun <V> Column<V>.default(value: V): Column<V> = table.replaceColumn(this, DefaultValueColumn(this, value))


fun Name.referenceName(): String = when (this) {
    is Identifier -> id
    is QualifiedIdentifier<*> -> "${parent.referenceName()}_${identifier.referenceName()}"
    else -> error("Unknown Name '$this'")
}

fun <V> TableDefinition.reference(column: Column<V>, name: String? = null): Column<V> {
    return createColumn(name ?: "${column.name.referenceName()}", ReferenceColumnType(column))
}

fun TableDefinition.integer(name: String): Column<Int> {
    return createColumn(name, IntColumnType)
}

fun TableDefinition.char(name: String): Column<Char> {
    return createColumn(name, CharColumnType)
}

inline fun <reified V : Enum<V>> TableDefinition.enumeration(name: String): Column<V> {
    return createColumn(name, EnumColumnType(V::class.java))
}

fun TableDefinition.decimal(name: String, scale: Int, precision: Int): Column<BigDecimal> {
    return createColumn(name, DecimalColumnType(scale, precision))
}

fun TableDefinition.long(name: String): Column<Long> {
    return createColumn(name, LongColumnType)
}

fun TableDefinition.date(name: String): Column<LocalDate> {
    return createColumn(name, DateColumnType)
}

fun TableDefinition.bool(name: String): Column<Boolean> {
    return createColumn(name, BooleanColumnType)
}

fun TableDefinition.datetime(name: String): Column<LocalDateTime> {
    return createColumn(name, DateTimeColumnType)
}

fun TableDefinition.blob(name: String): Column<Blob> { // TODO: It's java.sql, avoid
    return createColumn(name, BlobColumnType)
}

fun TableDefinition.text(name: String): Column<String> {
    return createColumn(name, StringColumnType())
}

fun TableDefinition.binary(name: String, length: Int): Column<ByteArray> {
    return createColumn(name, BinaryColumnType(length))
}

fun TableDefinition.uuid(name: String): Column<UUID> {
    return createColumn(name, UUIDColumnType)
}

fun TableDefinition.varchar(name: String, length: Int, collate: String? = null): Column<String> {
    return createColumn(name, StringColumnType(length, collate))
}
