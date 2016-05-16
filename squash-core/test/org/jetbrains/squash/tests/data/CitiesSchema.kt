package org.jetbrains.squash.tests.data

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.query.*
import org.jetbrains.squash.statements.*

object Cities : TableDefinition() {
    val id = integer("id").autoIncrement() // PKColumn<Int>
    val name = varchar("name", 50) // Column<String>
}

object Citizens : TableDefinition() {
    val id = varchar("id", 10).primaryKey() // PKColumn<String>
    val name = varchar("name", length = 50) // Column<String>
    val cityId = reference(Cities.id, "city_id").nullable() // Column<Int?>
}

object CitizenData : TableDefinition() {
    val citizen_id = reference(Citizens.id)
    val comment = varchar("comment", 30)
    val value = integer("value")
}

object Inhabitants : QueryObject {
    val cityName = Cities.name.alias("cityName")
    val citizenName = Citizens.name.alias("citizenName")

    override fun build(): QueryStatement = query()
            .from(Citizens)
            .innerJoin(Cities) { Cities.id eq Citizens.cityId }
            .select(citizenName, cityName)
}

