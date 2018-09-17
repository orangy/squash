package org.jetbrains.squash.definition

import java.time.OffsetDateTime

object OffsetDateTimeColumnType : ColumnType(OffsetDateTime::class)
object IntArrayColumnType : ColumnType(Array<Int>::class)
object TextArrayColumnType : ColumnType(Array<String>::class)
object JsonbColumnType : ColumnType(Json::class)