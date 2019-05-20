package org.jetbrains.squash.dialects.mysql.tests

import org.jetbrains.squash.dialects.mysql.expressions.*
import org.jetbrains.squash.expressions.alias
import org.jetbrains.squash.query.select
import org.jetbrains.squash.results.get
import org.jetbrains.squash.tests.DatabaseTests
import org.jetbrains.squash.tests.QueryTests
import org.jetbrains.squash.tests.data.AllColumnTypes
import org.jetbrains.squash.tests.data.withAllColumnTypes
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals

class MySqlQueryTests : QueryTests(), DatabaseTests by MySqlDatabaseTests() {
    override fun nullsLast(sql: String): String {
        return "ISNULL(${sql.removeSuffix(" DESC")}), $sql"
    }

	@Test
	fun mysqlDatePartFunctions() = withAllColumnTypes {
		val query = AllColumnTypes.select(
			AllColumnTypes.datetime.year().alias("yearPart"),
			AllColumnTypes.datetime.month().alias("monthPart"),
			AllColumnTypes.datetime.day().alias("dayPart"),
			AllColumnTypes.datetime.dayOfWeek().alias("dayOfWeekPart"),
			AllColumnTypes.datetime.weekDay().alias("weekDayPart"),
			AllColumnTypes.datetime.dayOfYear().alias("dayOfYearPart"),
			AllColumnTypes.datetime.date().alias("dateOnly")
		)
		
		connection.dialect.statementSQL(query).assertSQL {
			"SELECT YEAR(AllColumnTypes.datetime) AS yearPart, MONTH(AllColumnTypes.datetime) AS monthPart, DAYOFMONTH(AllColumnTypes.datetime) AS dayPart, DAYOFWEEK(AllColumnTypes.datetime) AS dayOfWeekPart, WEEKDAY(AllColumnTypes.datetime) AS weekDayPart, DAYOFYEAR(AllColumnTypes.datetime) AS dayOfYearPart, DATE(AllColumnTypes.datetime) AS dateOnly FROM AllColumnTypes"
		}
		
		val result = query.execute().single()
		assertEquals(1976, result["yearPart"], "YEAR() result is incorrect")
		assertEquals(11, result["monthPart"], "MONTH() result is incorrect")
		assertEquals(24, result["dayPart"], "DAYOFMONTH() result is incorrect")
		assertEquals(4, result["dayOfWeekPart"], "DAYOFWEEK() result is incorrect")
		assertEquals(2, result["weekDayPart"], "WEEKDAY() result is incorrect")
		assertEquals(329, result["dayOfYearPart"], "DAYOFYEAR() result is incorrect")
	}

	@Test
	fun mysqlDateMathFunctions() = withAllColumnTypes {
		val query = AllColumnTypes.select(
			AllColumnTypes.datetime.alias("originalDate"),
			
			AllColumnTypes.date.dateSub(1).alias("minusDays"),
			AllColumnTypes.datetime.dateSub(3, ChronoUnit.HOURS).alias("minusHours"),
			
			AllColumnTypes.date.dateAdd(1).alias("plusDays"),
			AllColumnTypes.datetime.dateAdd(3, ChronoUnit.HOURS).alias("plusHours")
		)

		connection.dialect.statementSQL(query).assertSQL {
			"SELECT AllColumnTypes.datetime AS originalDate, DATE_SUB(AllColumnTypes.`date`, INTERVAL ? DAY) AS minusDays, DATE_SUB(AllColumnTypes.datetime, INTERVAL ? HOUR) AS minusHours, DATE_ADD(AllColumnTypes.`date`, INTERVAL ? DAY) AS plusDays, DATE_ADD(AllColumnTypes.datetime, INTERVAL ? HOUR) AS plusHours FROM AllColumnTypes"
		}
		
		val result = query.execute().single()

		assertEquals(LocalDate.of(1976, 11, 23), result["minusDays"], "dateSub(days) result is incorrect")
		assertEquals(LocalDateTime.of(1976, 11, 24, 5, 22), result["minusHours"], "dateSub(3, hours) result is incorrect")
		assertEquals(LocalDate.of(1976, 11, 25), result["plusDays"], "dateAdd(days) result is incorrect")
		assertEquals(LocalDateTime.of(1976, 11, 24, 11, 22), result["plusHours"], "dateAdd(3, hours) result is incorrect")
	}
}