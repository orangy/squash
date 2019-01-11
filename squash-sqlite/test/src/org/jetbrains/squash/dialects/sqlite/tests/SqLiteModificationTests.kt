package org.jetbrains.squash.dialects.sqlite.tests

import org.jetbrains.squash.tests.*

class SqLiteModificationTests : ModificationTests(), DatabaseTests by SqLiteDatabaseTests()