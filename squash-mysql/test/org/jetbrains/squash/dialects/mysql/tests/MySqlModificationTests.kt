package org.jetbrains.squash.dialects.mysql.tests

import org.jetbrains.squash.tests.*

class MySqlModificationTests : ModificationTests(), DatabaseTests by MySqlDatabaseTests()