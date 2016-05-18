package org.jetbrains.squash.results

import org.jetbrains.squash.definition.*

interface ResponseRow {
    operator fun <V> get(column: Column<V>): V
    fun <V> get(name: String): V
    fun <V> get(index: Int): V
}