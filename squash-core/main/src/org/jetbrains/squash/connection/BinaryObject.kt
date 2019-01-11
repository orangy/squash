package org.jetbrains.squash.connection

interface BinaryObject {
    companion object {
        fun fromByteArray(transaction: Transaction, bytes: ByteArray): BinaryObject {
            return transaction.createBlob(bytes)
        }
    }

    val bytes: ByteArray
}