@file:JvmName("Columns")

package org.jetbrains.squash.definition

import org.jetbrains.squash.connection.*
import java.math.*
import java.time.*
import java.util.*


/**
 * Creates a column referencing another column in a different table
 */
fun <V> TableDefinition.reference(column: ColumnDefinition<V>, name: String? = null): ReferenceColumn<V> {
    val referenceName = name ?: column.name.referenceName()
    val reference = addColumn(ReferenceColumn<V>(this, Identifier(referenceName), column))
    constraints.add(ForeignKeyConstraint(Identifier("FK_${compoundName.id}_$referenceName"), listOf(reference), listOf(column)))
    return reference
}

/**
 * Creates an [Int] column
 */
fun TableDefinition.integer(name: String): ColumnDefinition<Int> {
    return createColumn(name, IntColumnType)
}

/**
 * Creates a [String] column, representing fixed-length text
 */
fun TableDefinition.char(name: String, length: Int = 1): ColumnDefinition<String> {
    return createColumn(name, CharColumnType(length))
}

/**
 * Creates an [Enum] column
 */
inline fun <reified V : Enum<V>> TableDefinition.enumeration(name: String): ColumnDefinition<V> {
    return createColumn(name, EnumColumnType(V::class))
}

/**
 * Creates a [BigDecimal] column
 */
fun TableDefinition.decimal(name: String, scale: Int, precision: Int): ColumnDefinition<BigDecimal> {
    return createColumn(name, DecimalColumnType(scale, precision))
}

/**
 * Creates a [Long] column
 */
fun TableDefinition.long(name: String): ColumnDefinition<Long> {
    return createColumn(name, LongColumnType)
}

/**
 * Creates a [LocalDate] column
 */
fun TableDefinition.date(name: String): ColumnDefinition<LocalDate> {
    return createColumn(name, DateColumnType)
}

/**
 * Creates a [Boolean] column
 */
fun TableDefinition.bool(name: String): ColumnDefinition<Boolean> {
    return createColumn(name, BooleanColumnType)
}

/**
 * Creates a [LocalDateTime] column
 */
fun TableDefinition.datetime(name: String): ColumnDefinition<LocalDateTime> {
    return createColumn(name, DateTimeColumnType)
}

/**
 * Creates a [String] column, using CBlob or other appropriate database type for large text
 */
fun TableDefinition.text(name: String): ColumnDefinition<String> {
    return createColumn(name, StringColumnType())
}

/**
 * Creates a [ByteArray] column
 */
fun TableDefinition.binary(name: String, length: Int): ColumnDefinition<ByteArray> {
    return createColumn(name, BinaryColumnType(length))
}

/**
 * Creates an [UUID] column
 */
fun TableDefinition.uuid(name: String): ColumnDefinition<UUID> {
    return createColumn(name, UUIDColumnType)
}

/**
 * Creates an BLOB column
 */
fun TableDefinition.blob(name: String): ColumnDefinition<BinaryObject> {
    return createColumn(name, BlobColumnType)
}

/**
 * Creates a [String] column, representing variable-length text, up to [length] characters
 */
fun TableDefinition.varchar(name: String, length: Int, collate: String? = null): ColumnDefinition<String> {
    return createColumn(name, StringColumnType(length, collate))
}
