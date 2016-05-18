package org.jetbrains.squash.dialects.h2.tests

import org.jetbrains.squash.tests.*

class H2SchemaTests : SchemaTests(), DatabaseTests by H2DatabaseTests()