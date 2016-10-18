package org.jetbrains.squash.tests.data

import org.jetbrains.squash.connection.*
import org.jetbrains.squash.definition.*
import org.jetbrains.squash.statements.*
import org.jetbrains.squash.tests.*

object HierarchyTable : TableDefinition() {
    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", 20)
    val parent_id = reference(id, "parent_id").nullable()
}

interface Hierarchy {
    val name: String
    val parent: Hierarchy?
    val children: List<Hierarchy>
}

fun <R> DatabaseTests.withHierarchy(statement: Transaction.() -> R): R {
    return withTables(HierarchyTable) {
        val rootId = insertInto(HierarchyTable).values {
            it[name] = "!"
        }.fetch(HierarchyTable.id).execute()

        val A = insertInto(HierarchyTable).values {
            it[name] = "A"
            it[parent_id] = rootId
        }.fetch(HierarchyTable.id).execute()

        val B = insertInto(HierarchyTable).values {
            it[name] = "B"
            it[parent_id] = rootId
        }.fetch(HierarchyTable.id).execute()

        insertInto(HierarchyTable).values {
            it[name] = "A1"
            it[parent_id] = A
        }.execute()

        insertInto(HierarchyTable).values {
            it[name] = "B1"
            it[parent_id] = B
        }.execute()

        statement()
    }
}
