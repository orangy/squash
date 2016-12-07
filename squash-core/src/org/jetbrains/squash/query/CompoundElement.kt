package org.jetbrains.squash.query

import org.jetbrains.squash.definition.*

interface CompoundElement

interface NamedCompoundElement : CompoundElement {
    val compoundName: Name
}