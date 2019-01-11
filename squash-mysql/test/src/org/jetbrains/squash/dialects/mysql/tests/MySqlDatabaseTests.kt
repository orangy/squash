package org.jetbrains.squash.dialects.mysql.tests

import com.wix.mysql.*
import com.wix.mysql.EmbeddedMysql.*
import com.wix.mysql.config.*
import com.wix.mysql.config.MysqldConfig.*
import com.wix.mysql.distribution.*
import org.jetbrains.squash.connection.*
import org.jetbrains.squash.definition.*
import org.jetbrains.squash.dialects.mysql.*
import org.jetbrains.squash.tests.*
import java.util.*
import kotlin.test.*

//val mariadb = DB.newEmbeddedDB(3306).also { it.start() }
val config: MysqldConfig = aMysqldConfig(Version.v5_7_latest)
        .withPort(3306)
        .withUser("user", "")
        .withTimeZone(TimeZone.getDefault())
        .build()

val mysql: EmbeddedMysql = anEmbeddedMysql(config)
        .addSchema("test")
        .start()

class MySqlDatabaseTests : DatabaseTests {
    override val quote = "`"
    override val indexIfNotExists: String = ""
    override val blobType = "BLOB"
    override fun getIdColumnType(columnType: ColumnType): String = when (columnType) {
        is IntColumnType -> "INT NOT NULL AUTO_INCREMENT"
        is LongColumnType -> "BIGINT NOT NULL AUTO_INCREMENT"
        else -> fail("Unsupported column type $columnType")
    }

    override fun primaryKey(name: String, vararg column: String): String = ", CONSTRAINT PK_$name PRIMARY KEY (${column.joinToString()})"
    override fun autoPrimaryKey(table: String, column: String): String = primaryKey(table, column)

    override fun createConnection(): DatabaseConnection {
        mysql.reloadSchema("test")
        return MySqlConnection.create("jdbc:mysql://localhost:${mysql.config.port}/test?useSSL=false", "user", "")
    }
}