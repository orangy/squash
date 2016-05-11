package org.jetbrains.squash.results

import org.jetbrains.squash.definition.*
import kotlin.internal.*

interface Response {

}

interface ResponseRow {
    operator fun <V> get(column: Column<@Exact V>): V
    fun <V> get(name: String): V
}