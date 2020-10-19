[![JetBrains team project](http://jb.gg/badges/team.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![Download](https://api.bintray.com/packages/kotlin/squash/squash/images/download.svg) ](https://bintray.com/kotlin/squash/squash/_latestVersion)
[![TeamCity (simple build status)](https://img.shields.io/teamcity/http/teamcity.jetbrains.com/s/KotlinTools_Squash_Build.svg)](https://teamcity.jetbrains.com/viewType.html?buildTypeId=KotlinTools_Squash_Build&branch_KotlinTools_Squash=%3Cdefault%3E&tab=buildTypeStatusDiv)
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)

Squash
------

Squash is a data access and manipulation DSL library for relational databases.

* *Strongly typed*. IDE and compiler knows how to verify your queries, assist in query editing and navigate.
* *No code generation*. Data structure definitions are done in code and validated against database actual schema.
  Schema can also be generated from definitions, which is ideal for fast prototyping and tests.
* *Unopinionated*. There is no prescribed way on how to manage your transactions, connections, or data objects.
* *Extensible*. Connection, that manages SQL execution and result set mapping, can be extended to support specific database engine needs.
  Dialect, responsible for building relevant SQL statements and queries, can be overridden or replaced to support specific SQL language variants.
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

Connect to a database:

```kotlin
import org.jetbrains.squash.connection.DatabaseConnection
import org.jetbrains.squash.connection.transaction
import org.jetbrains.squash.dialects.mysql.MySqlConnection

val db: DatabaseConnection = MySqlConnection.create("jdbc:mysql://localhost:3306/test", "test", "123")
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
val row = from(Citizens)
    .where { Citizens.id eq "eugene" }
    .select(Citizens.name, Citizens.id)
    .execute()
    .single()

assertEquals("eugene", row[Citizens.id])
assertEquals("Eugene", row[Citizens.name])
```

Logical operators:

```kotlin
val row = from(Citizens)
    .select(Citizens.name, Citizens.id)
    .where { 
        Citizens.name eq "Eugene" and
        (Citizens.cityId eq munichId)
    }
    .execute()
    .single()
```

Update Data:

```kotlin
update(Citizens)
    .set{
        it[cityId] = londonId
    }
    .where{ Citizens.id eq "eugene" }
    .execute()
```


Join:

```kotlin
from(Citizens)
    .innerJoin(Cities) { Cities.id eq Citizens.cityId }
    .select(Citizens.name, Cities.name)
```

Transactions:

```kotlin
db.transaction{
    insertInto(Citizens).values {
        it[id] = "steven"
        it[name] = "Steven"
        it[cityId] = newYorkId
    }.execute()
    
    insertInto(Citizens).values {
        it[id] = "natacha"
        it[name] = "Natacha"
        it[cityId] = moscowId
    }.execute()
}
```
