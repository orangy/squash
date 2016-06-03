package org.jetbrains.squash.dialects.sqlite.tests

import org.jetbrains.squash.tests.*

class SqLiteDefinitionTests : DefinitionTests(), DatabaseTests by SqLiteDatabaseTests()