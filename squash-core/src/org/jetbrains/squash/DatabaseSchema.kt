package org.jetbrains.squash

interface DatabaseSchema {
    interface Table {
        val name: String
        open fun columns(): Sequence<Column>
    }

    interface Column {
        val name: String
        val size: Int
        val nullable: Boolean
        val autoIncrement: Boolean

    }

    open fun tables(): Sequence<Table>
}