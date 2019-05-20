package org.jetbrains.squash.tests.data

import org.jetbrains.squash.connection.BinaryObject
import org.jetbrains.squash.connection.Transaction
import org.jetbrains.squash.definition.*
import org.jetbrains.squash.statements.insertInto
import org.jetbrains.squash.statements.values
import org.jetbrains.squash.tests.DatabaseTests
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

enum class E {
    ONE,
    TWO,
    THREE
}

object AllColumnTypes : TableDefinition() {
    val id = integer("id").autoIncrement().primaryKey()
    val varchar = varchar("varchar", 42)
    val char = char("char")
    val enum = enumeration<E>("enum")
    val decimal = decimal("decimal", 5, 2)
    val long = long("long")
    val date = date("date")
    val bool = bool("bool")
    val datetime = datetime("datetime")
    val text = text("text")
    val binary = binary("binary", 128)
    val blob = blob("blob")
    val uuid = uuid("uuid")
}

fun <R> DatabaseTests.withAllColumnTypes(statement:Transaction.() -> R) :R {
	return withTables(AllColumnTypes) {
		insertInto(AllColumnTypes).values {
			it[varchar] = "varchar"
			it[char] = "c"
			it[enum] = E.ONE
			it[decimal] = BigDecimal.ONE
			it[long] = 222L
			it[date] = LocalDate.of(1976, 11, 24)
			it[bool] = true
			it[datetime] = LocalDateTime.of(LocalDate.of(1976, 11, 24), LocalTime.of(8, 22))
			it[text] = "Lorem Ipsum"
			it[binary] = byteArrayOf(1, 2, 3)
			it[blob] = BinaryObject.fromByteArray(this@withTables, byteArrayOf(1, 2, 3))
			it[uuid] = UUID.fromString("7cb64fe4-4938-4e88-8d94-17e929d40c99")
		}.execute()
		
		statement()
	}
}