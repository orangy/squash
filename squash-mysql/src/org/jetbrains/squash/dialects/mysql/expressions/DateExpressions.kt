package org.jetbrains.squash.dialects.mysql.expressions

import org.jetbrains.squash.dialects.mysql.expressions.MysqlTimeUnit.Companion.from
import org.jetbrains.squash.expressions.Expression
import org.jetbrains.squash.expressions.FunctionExpression
import org.jetbrains.squash.expressions.GeneralFunctionExpression
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 *  Extract the date part of a date or datetime expression
 */
fun Expression<*>.date() = GeneralFunctionExpression<LocalDate>("DATE", this)

/**
 *  Subtract a time value (using Java [ChronoUnit]) from a date
 */
fun Expression<*>.dateSub(value:Long, timeUnit:ChronoUnit = ChronoUnit.DAYS) = MysqlDateMathFunction("DATE_SUB", this, from(value, timeUnit))

/**
 * Subtracts a number of days from a date.
 */
fun Expression<*>.dateSub(days:Long) = MysqlDateMathFunction("DATE_SUB", this, MysqlTimeUnit(days, "DAY"))

/**
 *  Add a time value (using Java [ChronoUnit]) to a date
 */
fun Expression<*>.dateAdd(value:Long, timeUnit:ChronoUnit = ChronoUnit.DAYS) = MysqlDateMathFunction("DATE_ADD", this, from(value, timeUnit))

/**
 * Adds a number of days to a date.
 */
fun Expression<*>.dateAdd(days:Long) = MysqlDateMathFunction("DATE_ADD", this, MysqlTimeUnit(days, "DAY"))

/**
 * Return the year part of a date.
 */
fun Expression<*>.year() = GeneralFunctionExpression<Int>("YEAR", this)

/**
 *  Return the month from the date passed.
 */
fun Expression<*>.month() = GeneralFunctionExpression<Int>("MONTH", this)

/**
 * Return the day of the month part of a date.
 */
fun Expression<*>.day() = dayOfMonth()

/**
 * Return the day of the month part of a date.
 */
fun Expression<*>.dayOfMonth() = GeneralFunctionExpression<Int>("DAYOFMONTH", this)

/**
 *  Return the numeric weekday from a date (1 = Sunday, 2 = Monday, …, 7 = Saturday).
 *  This function conforms to ODBC standard.
 */
fun Expression<*>.dayOfWeek() = GeneralFunctionExpression<Int>("DAYOFWEEK", this)

/**
 *  Return the numeric weekday index from a date (0 = Monday, 1 = Tuesday, … 6 = Sunday).
 */
fun Expression<*>.weekDay() = GeneralFunctionExpression<Int>("WEEKDAY", this)

/**
 * Return the numeric day of the year from a date.
 */
fun Expression<*>.dayOfYear() = GeneralFunctionExpression<Int>("DAYOFYEAR", this)

/*
 * Date Math
 */

class MysqlDateMathFunction(val name:String, val expression:Expression<*>, val interval: MysqlTimeUnit) : FunctionExpression<LocalDateTime>

class MysqlTimeUnit(
	val value:Long,
	val unit:String
) {
	
	override fun toString():String = "INTERVAL $value $unit"
	
	companion object {

		/**
		 * Creates a [MysqlTimeUnit] value from a Java [ChronoUnit].
		 */
		fun from(value:Long, timeUnit:ChronoUnit) = when (timeUnit) {
			ChronoUnit.MICROS -> MysqlTimeUnit(value, "MICROSECOND")
			ChronoUnit.SECONDS -> MysqlTimeUnit(value, "SECOND")
			ChronoUnit.MINUTES -> MysqlTimeUnit(value, "MINUTE")
			ChronoUnit.HOURS -> MysqlTimeUnit(value, "HOUR")
			ChronoUnit.DAYS -> MysqlTimeUnit(value, "DAY")
			ChronoUnit.MONTHS -> MysqlTimeUnit(value, "MONTH")
			ChronoUnit.YEARS -> MysqlTimeUnit(value, "YEAR")
			ChronoUnit.WEEKS -> MysqlTimeUnit(value, "WEEK")
			else -> error("ChronoUnit not supported by MySQL intervals.")
		}
	}
}