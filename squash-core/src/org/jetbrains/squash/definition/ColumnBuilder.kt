@file:JvmName("Columns")

package org.jetbrains.squash.definition

import org.jetbrains.squash.connection.*
import java.math.*
import java.time.*
import java.util.*

/**
 * Modifies this [Int] column to represent an automatically incrementing [Column]
 */
fun Column<Int>.autoIncrement(): Column<Int> = table.replaceColumn(this, AutoIncrementColumn(this))

/**
 * Modifies this [Long] column to represent an automatically incrementing [Column]
 */
@JvmName("autoIncrementLong")
fun Column<Long>.autoIncrement(): Column<Long> = table.replaceColumn(this, AutoIncrementColumn(this))

/**
 * Modifies this column to represent a nullable [Column]
 */
fun <V> Column<V>.nullable(): Column<V?> = table.replaceColumn(this, NullableColumn(this))

/**
 * Modifies this column to represent a [Column] with a default [value]
 */
fun <V> Column<V>.default(value: V): Column<V> = table.replaceColumn(this, DefaultValueColumn(this, value))

/**
 * Creates a column referencing another column in a different table
 */
fun <V> TableDefinition.reference(column: Column<V>, name: String? = null): Column<V> {
    return createColumn(name ?: column.name.referenceName(), ReferenceColumnType(column))
}

/**
 * Creates an [Int] column
 */
fun TableDefinition.integer(name: String): Column<Int> {
    return createColumn(name, IntColumnType)
}

/**
 * Creates a [String] column, representing fixed-length text
 */
fun TableDefinition.char(name: String, length: Int = 1): Column<String> {
    return createColumn(name, CharColumnType(length))
}

/**
 * Creates an [Enum] column
 */
inline fun <reified V : Enum<V>> TableDefinition.enumeration(name: String): Column<V> {
    return createColumn(name, EnumColumnType(V::class.java))
}

/**
 * Creates a [BigDecimal] column
 */
fun TableDefinition.decimal(name: String, scale: Int, precision: Int): Column<BigDecimal> {
    return createColumn(name, DecimalColumnType(scale, precision))
}

/**
 * Creates a [Long] column
 */
fun TableDefinition.long(name: String): Column<Long> {
    return createColumn(name, LongColumnType)
}

/**
 * Creates a [LocalDate] column
 */
fun TableDefinition.date(name: String): Column<LocalDate> {
    return createColumn(name, DateColumnType)
}

/**
 * Creates a [Boolean] column
 */
fun TableDefinition.bool(name: String): Column<Boolean> {
    return createColumn(name, BooleanColumnType)
}

/**
 * Creates a [LocalDateTime] column
 */
fun TableDefinition.datetime(name: String): Column<LocalDateTime> {
    return createColumn(name, DateTimeColumnType)
}

/**
 * Creates a [String] column, using CBlob or other appropriate database type for large text
 */
fun TableDefinition.text(name: String): Column<String> {
    return createColumn(name, StringColumnType())
}

/**
 * Creates a [ByteArray] column
 */
fun TableDefinition.binary(name: String, length: Int): Column<ByteArray> {
    return createColumn(name, BinaryColumnType(length))
}

/**
 * Creates an [UUID] column
 */
fun TableDefinition.uuid(name: String): Column<UUID> {
    return createColumn(name, UUIDColumnType)
}

/**
 * Creates an BLOB column
 */
fun TableDefinition.blob(name: String): Column<BinaryObject> {
    return createColumn(name, BlobColumnType)
}

/**
 * Creates a [String] column, representing variable-length text, up to [length] characters
 */
fun TableDefinition.varchar(name: String, length: Int, collate: String? = null): Column<String> {
    return createColumn(name, StringColumnType(length, collate))
}
