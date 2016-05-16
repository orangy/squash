package org.jetbrains.squash.tests.data

import org.jetbrains.squash.definition.*

enum class E {
    ONE,
    TWO,
    THREE
}

object Misc : TableDefinition() {
    val n = integer("n")
    val nn = integer("nn").nullable()

    val d = date("d")
    val dn = date("dn").nullable()

    val t = datetime("t")
    val tn = datetime("tn").nullable()

    val e = enumeration<E>("e")
    val en = enumeration<E>("en").nullable()

    val s = varchar("s", 100)
    val sn = varchar("sn", 100).nullable()

    val dc = decimal("dc", 12, 2)
    val dcn = decimal("dcn", 12, 2).nullable()

}
