package org.jetbrains.squash.benchmarks

import com.wix.mysql.*
import com.wix.mysql.EmbeddedMysql.*
import com.wix.mysql.config.*
import com.wix.mysql.config.MysqldConfig.*
import com.wix.mysql.distribution.*
import org.jetbrains.squash.connection.*
import org.jetbrains.squash.dialects.mysql.*
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.annotations.Setup

private val config: MysqldConfig = aMysqldConfig(Version.v5_7_latest)
        .withPort(3306)
        .withUser("user", "")
        .withServerVariable("innodb_flush_log_at_trx_commit", 0)
        .build()

open class MySqlQueryBenchmark : QueryBenchmark() {
    lateinit var mysql: EmbeddedMysql

    @Setup
    fun startMySQL() {
        mysql = anEmbeddedMysql(config)
                .addSchema("test")
                .start()
    }

    @TearDown
    fun stopMySQL() {
        mysql.stop()
    }

    override fun createConnection(): DatabaseConnection = MySqlConnection.create("jdbc:mysql://localhost:${mysql.config.port}/test?useSSL=false", "user", "")
}