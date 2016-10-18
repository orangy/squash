package org.jetbrains.squash.dialects.sqlite.tests

import org.jetbrains.squash.tests.*

class SqLiteGraphTests : GraphTests(), DatabaseTests by SqLiteDatabaseTests()