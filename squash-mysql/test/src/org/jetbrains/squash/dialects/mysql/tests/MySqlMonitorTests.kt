package org.jetbrains.squash.dialects.mysql.tests

import org.jetbrains.squash.tests.*

class MySqlMonitorTests : MonitorTests(), DatabaseTests by MySqlDatabaseTests()