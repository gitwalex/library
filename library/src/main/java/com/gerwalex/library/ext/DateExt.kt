package com.gerwalex.library.ext

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Formats this date to a string using the provided format pattern.
 *
 * This extension function simplifies formatting a `Date` object into a `String`
 * by using a `SimpleDateFormat` instance with the specified pattern.
 *
 * Example:
 * ```kotlin
 * val now = Date()
 * val formattedDate = now.formatTo("yyyy-MM-dd HH:mm:ss")
 * println(formattedDate) // e.g., "2023-10-27 10:30:00"
 * ```
 *
 * @param format The format pattern string (e.g., "dd.MM.yyyy").
 * @return The formatted date as a `String`.
 */
fun Date.formatTo(pattern: String = "yyyy-MM-dd"): String {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(this)
}

/**
 * Converts a Date object to a Calendar object.
 *
 * @return A [java.util.Calendar] instance representing the same point in time as this Date.
 */
fun Date.toCalendar(): Calendar {
    return Calendar.getInstance().apply {
        time = this@toCalendar
    }
}

/**
 * Adds a specified number of days to a Date.
 *
 * @param days The number of days to add. Defaults to 1.
 * @return A new Date object representing the result.
 */
fun Date.addDays(days: Int): Date {
    return toCalendar().apply {
        add(Calendar.DAY_OF_MONTH, days)
    }.time
}

/**
 * Adds a specified number of hours to a Date.
 *
 * @param hours The number of hours to add.
 * @return A new Date object representing the result.
 */
fun Date.addHours(hours: Int): Date {
    return toCalendar().apply {
        add(Calendar.HOUR_OF_DAY, hours)
    }.time
}

/**
 * Checks if this Date is today.
 * @return true if this Date represents a day in the current calendar date, ignoring the time component.
 */
fun Date.isToday(): Boolean {
    val today = Calendar.getInstance()
    val dateCalendar = toCalendar()
    return today.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR) &&
            today.get(Calendar.DAY_OF_YEAR) == dateCalendar.get(Calendar.DAY_OF_YEAR)
}

/**
 * Checks if this [java.util.Date] is in the future.
 *
 * @return `true` if this date is after the current time, `false` otherwise.
 */
fun Date.isFuture(): Boolean {
    return time > System.currentTimeMillis()
}

/**
 * @return true, if this date is in the past.
 */
fun Date.isPast(): Boolean {
    return time < System.currentTimeMillis()
}

/**
 * Checks if this Date falls on a weekend (Saturday or Sunday).
 *
 * This function uses the `Calendar` class to determine the day of the week
 * for the given `Date` and checks if it is `Calendar.SATURDAY` or `Calendar.SUNDAY`.
 * The definition of a weekend is based on the default `Locale` and `TimeZone`
 * of the system.
 *
 * @return `true` if this date is a Saturday or Sunday, `false` otherwise.
 */
fun Date.isWeekend(): Boolean {
    return this.toCalendar().let {
        it.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || it.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
    }
}

/**
 * Checks if this Date falls on a weekday (Monday to Friday).
 *
 * This function uses a [Calendar] instance to determine the day of the week
 * for this [Date]. It returns `true` if the day is not Saturday or Sunday.
 *
 * @return `true` if the date is a weekday, `false` if it is a Saturday or Sunday.
 */
fun Date.isWeekDay(): Boolean {
    return !this.isWeekend()
}

/**
 * Formats this date to a string using the provided format pattern.
 *
 * This extension function simplifies formatting a `Date` object into a `String`
 * by using a `SimpleDateFormat` instance with the specified pattern and the default `Locale`.
 *
 * Example:
 * ```kotlin
 * val now = Date()
 * val formattedDate = now.formatTo("yyyy-MM-dd HH:mm:ss")
 * println(formattedDate) // e.g., "2023-10-27 10:30:00"
 *
 * val formattedWithDefault = now.formatTo()
 * println(formattedWithDefault) // e.g., "2023-10-27"
 * ```
 *
 * @param pattern The format pattern string (e.g., "dd.MM.yyyy"). Defaults to "yyyy-MM-dd".
 * @return The formatted date as a `String`.
 */
@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.formatTo(pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
    return format(DateTimeFormatter.ofPattern(pattern))
}

/**
 * Checks if this Date falls on a weekend (Saturday or Sunday).
 *
 * This function uses the `Calendar` class to determine the day of the week
 * for the given `Date`.
 *
 * @return `true` if the date is a Saturday or a Sunday, `false` otherwise.
 */
@RequiresApi(Build.VERSION_CODES.O)
fun LocalDate.isWeekend(): Boolean {
    return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY
}

/**
 * Checks if this Date falls on a weekday (Monday to Friday).
 *
 * This function uses a `Calendar` instance to determine the day of the week.
 * It considers `Calendar.MONDAY`, `Calendar.TUESDAY`, `Calendar.WEDNESDAY`,
 * `Calendar.THURSDAY`, and `Calendar.FRIDAY` as weekdays.
 *
 * @return `true` if the date is a weekday, `false` if it is a Saturday or Sunday.
 */
@RequiresApi(Build.VERSION_CODES.O)
fun LocalDate.isWeekday(): Boolean {
    return !isWeekend()
}

/**
 * Converts this [java.util.Date] object to the number of milliseconds from the epoch of 1970-01-01T00:00:00Z.
 *
 * This is equivalent to calling `this.time`.
 *
 * @return The number of milliseconds since the epoch.
 */
@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.toEpochMillis(): Long {
    return atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

/**
 * Converts this [java.util.Date] object to a [java.time.LocalDateTime].
 *
 * This extension function provides a convenient way to transition from the older `java.util.Date`
 * API to the modern `java.time` API. It first converts the `Date` to an `Instant`,
 * then uses the system's default time zone to create a `LocalDateTime`.
 *
 * @return A [java.time.LocalDateTime] instance representing the same date and time
 *         in the system's default time zone.
 */
@RequiresApi(Build.VERSION_CODES.O)
fun Long.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
}