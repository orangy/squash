package org.jetbrains.squash.dialect

import org.jetbrains.squash.definition.*

class SQLArgument<out V>(val columnType: ColumnType, val index: Int, val value: V)