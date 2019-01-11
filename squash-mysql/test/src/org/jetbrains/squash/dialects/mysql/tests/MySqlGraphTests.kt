package org.jetbrains.squash.dialects.mysql.tests

import org.jetbrains.squash.tests.*

class MySqlGraphTests : GraphTests(), DatabaseTests by MySqlDatabaseTests()