package org.jetbrains.squash.dialects.mysql.tests

import org.jetbrains.squash.tests.*

class MySqlDefinitionTests : DefinitionTests(), DatabaseTests by MySqlDatabaseTests()