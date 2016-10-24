package org.jetbrains.squash.drivers

import org.jetbrains.squash.connection.*

class JDBCBinaryObject(override val bytes: ByteArray) : BinaryObject {
    override fun toString(): String = "BLOB(${bytes.size}"
}