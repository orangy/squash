package org.jetbrains.squash.tests.data

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.query.*
import org.jetbrains.squash.statements.*

object Cities : TableDefinition() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)
}

object Citizens : TableDefinition() {
    val id = varchar("id", 10).primaryKey()
    val name = varchar("name", length = 50)
    val cityId = reference(Cities.id, "city_id").nullable()
}

object CitizenDataLink : TableDefinition() {
    val id = integer("id").autoIncrement()
    val citizen_id = reference(Citizens.id)
    val citizendata_id = reference(CitizenData.id)
}

object CitizenData : TableDefinition() {
    val id = integer("id").autoIncrement()
    val comment = varchar("comment", 30)
    val value = enumeration<DataKind>("value")
    val image = blob("image").nullable()
}

object Inhabitants : QueryObject {
    val cityName = Cities.name.alias("cityName")
    val citizenName = Citizens.name.alias("citizenName")

    override fun build(): QueryStatement = query()
            .from(Citizens)
            .innerJoin(Cities) { Cities.id eq Citizens.cityId }
            .select(citizenName, cityName)
}

enum class DataKind { Normal, Extended }