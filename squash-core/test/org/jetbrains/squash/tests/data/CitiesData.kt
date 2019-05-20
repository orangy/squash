package org.jetbrains.squash.tests.data

import org.jetbrains.squash.connection.*
import org.jetbrains.squash.expressions.*
import org.jetbrains.squash.query.*
import org.jetbrains.squash.statements.*
import org.jetbrains.squash.tests.*

fun <R> DatabaseTests.withCities(statement: Transaction.() -> R) :R {
    return withTables(Cities, CityStats, CitizenData, Citizens, CitizenDataLink) {
        val spbId = insertInto(Cities).values {
            it[name] = "St. Petersburg"
        }.fetch(Cities.id).execute()

        val munichId = insertInto(Cities).values {
            it[name] = "Munich"
        }.fetch(Cities.id).execute()

        val pragueId = insertInto(Cities).values {
            it[name] = "Prague"
        }.fetch(Cities.id).execute()
		
		/*
		 * Insert City Statistics
		 */

		insertInto(CityStats).values {
			it[cityId] = spbId
			it[name] = "population"
			it[value] = 6200000
		}.execute()
		
		insertInto(CityStats).values { 
			it[cityId] = munichId
			it[name] = "population"
			it[value] = 1500000
		}.execute()

		insertInto(CityStats).values {
			it[cityId] = pragueId
			it[name] = "population"
			it[value] = 2600000
		}.execute()

		/*
		 * Insert Citizens
		 */
        insertInto(Citizens).query()
                .select { literal("andrey").alias("id") }
                .select { literal("Andrey").alias("name") }
                .select { literal(spbId).alias("cityId") }
                .execute()

        insertInto(Citizens).values {
            it[id] = "sergey"
            it[name] = "Sergey"
            it[cityId] = munichId
        }.execute()

        insertInto(Citizens).values {
            it[id] = "eugene"
            it[name] = "Eugene"
            it[cityId] = munichId
        }.execute()

        insertInto(Citizens).values {
            it[id] = "alex"
            it[name] = "Alex"
            it[cityId] = null
        }.execute()

        insertInto(Citizens).values {
            it[id] = "smth"
            it[name] = "Something"
            it[cityId] = null
        }.execute()

        insertInto(CitizenData).values {
            it[comment] = "Something is here"
            it[value] = DataKind.Normal
        }.fetch(CitizenData.id).execute().let { ref ->
            insertInto(CitizenDataLink).values {
                it[citizen_id] = "smth"
                it[citizendata_id] = ref
            }.execute()
        }

        insertInto(CitizenData).values {
            it[comment] = "First comment for Eugene"
            it[value] = DataKind.Normal
        }.fetch(CitizenData.id).execute().let { ref ->
            insertInto(CitizenDataLink).values {
                it[citizen_id] = "eugene"
                it[citizendata_id] = ref
            }.execute()
        }
        insertInto(CitizenData).values {
            it[comment] = "Second comment for Eugene"
            it[value] = DataKind.Extended
            it[image] = BinaryObject.fromByteArray(this@withTables, byteArrayOf(1, 2, 3))
        }.fetch(CitizenData.id).execute().let { ref ->
            insertInto(CitizenDataLink).values {
                it[citizen_id] = "eugene"
                it[citizendata_id] = ref
            }.execute()
        }

        statement()
    }
}
