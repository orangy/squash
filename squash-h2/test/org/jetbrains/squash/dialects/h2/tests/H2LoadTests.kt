package org.jetbrains.squash.dialects.h2.tests

import org.jetbrains.squash.tests.*
import org.junit.*

@Ignore("Run it explicitly with profiler to find bottlenecks")
class H2LoadTests : LoadTests(), DatabaseTests by H2DatabaseTests()