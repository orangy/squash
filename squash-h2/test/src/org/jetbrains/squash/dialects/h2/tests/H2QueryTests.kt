package org.jetbrains.squash.dialects.h2.tests

import org.jetbrains.squash.tests.*

class H2QueryTests : QueryTests(), DatabaseTests by H2DatabaseTests()