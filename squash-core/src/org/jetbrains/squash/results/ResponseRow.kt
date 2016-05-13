package org.jetbrains.squash.results

import org.jetbrains.squash.definition.*
import kotlin.internal.*

interface Response {
    val rows : Sequence<ResponseRow>
}

interface ResponseRow {
    operator fun <V> get(column: Column<V>): V
    fun <V> get(name: String): V
}