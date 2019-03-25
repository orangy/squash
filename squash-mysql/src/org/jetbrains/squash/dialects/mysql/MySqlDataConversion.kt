package org.jetbrains.squash.dialects.mysql

import org.jetbrains.squash.connection.*
import org.jetbrains.squash.drivers.*
import java.nio.*
import java.sql.Connection
import java.util.*
import kotlin.reflect.*


class MySqlDataConversion : JDBCDataConversion() {
    override fun convertValueToDatabase(value: Any?, connection: Connection): Any? {
        if (value is UUID) {
            val bb = ByteBuffer.wrap(ByteArray(16))
            bb.putLong(value.mostSignificantBits)
            bb.putLong(value.leastSignificantBits)
            return bb.array()
        }
        return super.convertValueToDatabase(value, connection)
    }

    override fun convertValueFromDatabase(value: Any?, type: KClass<*>): Any? {
        if (value is ByteArray && type == UUID::class) {
            val bb = ByteBuffer.wrap(value)
            return UUID(bb.getLong(0), bb.getLong(8))
        }

        return super.convertValueFromDatabase(value, type)
    }
}