package org.jetbrains.squash.dialects.postgres.tests

import org.jetbrains.squash.tests.*

class PgModificationTests : ModificationTests(), DatabaseTests by PgDatabaseTests()