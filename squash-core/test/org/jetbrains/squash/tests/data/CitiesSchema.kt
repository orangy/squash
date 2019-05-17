package org.jetbrains.squash.tests.data

import org.jetbrains.squash.definition.*
import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.query.*
import org.jetbrains.squash.statements.*

object Cities : TableDefinition() {
    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", 50)
}

object CityStats : TableDefinition() {
	val cityId = reference(Cities.id, "cityId")
	val name = varchar("name", 50)
	val value = long("value")
}

object Citizens : TableDefinition() {
    val id = varchar("id", 10).primaryKey()
    val name = varchar("name", length = 50)
    val cityId = reference(Cities.id, "city_id").nullable()
}

object CitizenDataLink : TableDefinition() {
    val citizen_id = reference(Citizens.id)
    val citizendata_id = reference(CitizenData.id)
    init {
        primaryKey(citizen_id, citizendata_id)
    }
}

object CitizenData : TableDefinition() {
    val id = long("id").autoIncrement().primaryKey()
    val comment = varchar("comment", 30)
    val value = enumeration<DataKind>("value").index()
    val image = blob("image").nullable()
}

object Inhabitants : QueryObject {
    val cityName = Cities.name.alias("cityName")
    val citizenName = Citizens.name.alias("citizenName")

    override fun build(): QueryStatement = select(citizenName, cityName)
                    .from(Citizens)
                    .innerJoin(Cities) { Cities.id eq Citizens.cityId }
}

enum class DataKind { Normal, Extended }