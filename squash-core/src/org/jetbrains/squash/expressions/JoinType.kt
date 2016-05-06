package org.jetbrains.squash.expressions

enum class JoinType(val isInner: Boolean, val isOuter: Boolean) {
    DEFAULT(true, false),
    CROSS(false, false),
    INNER(true, false),
    LEFT(false, true),
    RIGHT(false, true),
    FULL(false, true)
}