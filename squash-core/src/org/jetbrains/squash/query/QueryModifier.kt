package org.jetbrains.squash.query

interface QueryModifier

class QueryLimit(val limit: Long, val offset: Long) : QueryModifier