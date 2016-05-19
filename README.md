Squash
------

Squash is a data access and manipulation DSL library for relational databases.

* *Strongly typed*. IDE and compiler knows how to verify your queries, assist in query editing and navigate.
* *No code generation*. Data structure definitions are done in code and validated against database actual schema.
  Schema can also be generated from definitions, which is ideal for fast prototyping and tests.
* *Unopinionated*. There is no prescribed way on how to manage your transactions, connections, or data objects.
* *Extensible*. Connection, that manages SQL execution and result set mapping, can be extended to support specific database engine needs.
  Dialect, responsible for building relevant SQL statements and queries, can be overriden or replaced to support specific SQL language variants.
* *Kotlin*.

Quick Samples
-------------

Define tables:

```kotlin
object Cities : TableDefinition() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)
}

object Citizens : TableDefinition() {
    val id = varchar("id", 10).primaryKey()
    val name = varchar("name", length = 50)
    val cityId = reference(Cities.id, "city_id").nullable()
}
```

Insert data:
```kotlin
insertInto(Citizens).values {
    it[id] = "eugene"
    it[name] = "Eugene"
    it[cityId] = munichId
}.execute()
```

Query tables:
```kotlin
val row = query(Citizens)
    .where { Citizens.id eq "eugene" }
    .select(Citizens.name, Citizens.id)
    .execute()
    .single()

assertEquals("eugene", row[Citizens.id])
assertEquals("Eugene", row[Citizens.name])
```

Join:
```kotlin
query(Citizens)
    .innerJoin(Cities) { Cities.id eq Citizens.cityId }
    .select(Citizens.name, Cities.name)
```
