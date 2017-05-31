package org.jetbrains.squash.tests.data

import org.jetbrains.squash.definition.*

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
