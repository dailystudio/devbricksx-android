package com.dailystudio.devbricksx.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class for common calendar and time operations.
 *
 * Provides methods to get start/end of day/week/month/year, extract date components,
 * and format time/duration strings.
 */
object CalendarUtils {

    const val FORMAT_TEPML_TIME = "HH:mm:ss"
    const val FORMAT_TEPML_DATETIME = "yyyy-MM-dd HH:mm:ss:SSS"

    const val SECOND_IN_MILLIS: Long = 1000
    const val MINUTE_IN_MILLIS = SECOND_IN_MILLIS * 60
    const val HOUR_IN_MILLIS = MINUTE_IN_MILLIS * 60
    const val DAY_IN_MILLIS = HOUR_IN_MILLIS * 24
    const val WEEK_IN_MILLIS = DAY_IN_MILLIS * 7
    const val YEAR_IN_MILLIS = WEEK_IN_MILLIS * 52

    private var sCalendar: Calendar = Calendar.getInstance()

    init {
        sCalendar.firstDayOfWeek = Calendar.MONDAY
    }

    /**
     * Gets the time of day from a timestamp (sets year/month/day to a fixed date).
     *
     * @param mills The timestamp.
     * @return The timestamp with year/month/day set to a fixed epoch reference.
     */
    @Synchronized
    fun getTimeOfDay(mills: Long): Long {
        sCalendar.timeInMillis = mills
        sCalendar.set(Calendar.YEAR, 2013)
        sCalendar.set(Calendar.MONTH, 14)
        sCalendar.set(Calendar.DAY_OF_MONTH, 8)

        return sCalendar.timeInMillis
    }

    /**
     * Sets the date part of a timestamp to match another date.
     *
     * @param mills The timestamp whose time component is preserved.
     * @param targetDate The timestamp whose date component is used.
     * @return The resulting timestamp.
     */
    @Synchronized
    fun setTimeOfDate(mills: Long, targetDate: Long): Long {
        sCalendar.timeInMillis = targetDate

        val year = sCalendar.get(Calendar.YEAR)
        val month = sCalendar.get(Calendar.MONTH)
        val day = sCalendar.get(Calendar.DAY_OF_MONTH)
        sCalendar.timeInMillis = mills
        sCalendar.set(Calendar.YEAR, year)
        sCalendar.set(Calendar.MONTH, month)
        sCalendar.set(Calendar.DAY_OF_MONTH, day)

        return sCalendar.timeInMillis
    }

    /**
     * Gets the start of the day (00:00:00.000) for a given timestamp.
     *
     * @param mills The timestamp.
     * @return The start of the day.
     */
    @Synchronized
    fun getStartOfDay(mills: Long): Long {
        sCalendar.timeInMillis = mills

        sCalendar.set(Calendar.HOUR_OF_DAY, 0)
        sCalendar.set(Calendar.MINUTE, 0)
        sCalendar.set(Calendar.SECOND, 0)
        sCalendar.set(Calendar.MILLISECOND, 0)

        return sCalendar.timeInMillis
    }

    /**
     * Gets the end of the day (23:59:59.999) for a given timestamp.
     *
     * @param mills The timestamp.
     * @return The end of the day.
     */
    @Synchronized
    fun getEndOfDay(mills: Long): Long {
        sCalendar.timeInMillis = mills

        sCalendar.set(Calendar.HOUR_OF_DAY, 23)
        sCalendar.set(Calendar.MINUTE, 59)
        sCalendar.set(Calendar.SECOND, 59)
        sCalendar.set(Calendar.MILLISECOND, 999)

        return sCalendar.timeInMillis
    }

    /**
     * Gets the start of the week (Monday 00:00:00.000) for a given timestamp.
     *
     * @param mills The timestamp.
     * @return The start of the week.
     */
    @Synchronized
    fun getStartOfWeek(mills: Long): Long {
        sCalendar.firstDayOfWeek = Calendar.MONDAY
        sCalendar.timeInMillis = mills

        sCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        sCalendar.set(Calendar.HOUR_OF_DAY, 0)
        sCalendar.set(Calendar.MINUTE, 0)
        sCalendar.set(Calendar.SECOND, 0)
        sCalendar.set(Calendar.MILLISECOND, 0)

        return sCalendar.timeInMillis
    }

    /**
     * Gets the end of the week (Sunday 23:59:59.999) for a given timestamp.
     *
     * @param mills The timestamp.
     * @return The end of the week.
     */
    @Synchronized
    fun getEndOfWeek(mills: Long): Long {
        val startMillis = getStartOfWeek(mills)

        sCalendar.timeInMillis = startMillis

        sCalendar.add(Calendar.DAY_OF_WEEK, 6)
        sCalendar.set(Calendar.HOUR_OF_DAY, 23)
        sCalendar.set(Calendar.MINUTE, 59)
        sCalendar.set(Calendar.SECOND, 59)
        sCalendar.set(Calendar.MILLISECOND, 999)

        return sCalendar.timeInMillis
    }

    /**
     * Gets the start of the month (1st day 00:00:00.000) for a given timestamp.
     *
     * @param mills The timestamp.
     * @return The start of the month.
     */
    @Synchronized
    fun getStartOfMonth(mills: Long): Long {
        sCalendar.timeInMillis = mills

        sCalendar.set(Calendar.DAY_OF_MONTH, 1)
        sCalendar.set(Calendar.HOUR_OF_DAY, 0)
        sCalendar.set(Calendar.MINUTE, 0)
        sCalendar.set(Calendar.SECOND, 0)
        sCalendar.set(Calendar.MILLISECOND, 0)

        return sCalendar.timeInMillis
    }

    /**
     * Gets the end of the month (Last day 23:59:59.999) for a given timestamp.
     *
     * @param mills The timestamp.
     * @return The end of the month.
     */
    @Synchronized
    fun getEndOfMonth(mills: Long): Long {
        sCalendar.timeInMillis = mills
        
        sCalendar.set(Calendar.DAY_OF_MONTH, 1)
        sCalendar.add(Calendar.MONTH, 1)
        sCalendar.add(Calendar.DAY_OF_MONTH, -1)
        sCalendar.set(Calendar.HOUR_OF_DAY, 23)
        sCalendar.set(Calendar.MINUTE, 59)
        sCalendar.set(Calendar.SECOND, 59)
        sCalendar.set(Calendar.MILLISECOND, 999)
        
        return sCalendar.timeInMillis
    }

    /**
     * Gets the start of the year (Jan 1st 00:00:00.000) for a given timestamp.
     *
     * @param mills The timestamp.
     * @return The start of the year.
     */
    @Synchronized
    fun getStartOfYear(mills: Long): Long {
        sCalendar.timeInMillis = mills

        sCalendar.set(Calendar.MONTH, Calendar.JANUARY)
        sCalendar.set(Calendar.DAY_OF_MONTH, 1)
        sCalendar.set(Calendar.HOUR_OF_DAY, 0)
        sCalendar.set(Calendar.MINUTE, 0)
        sCalendar.set(Calendar.SECOND, 0)
        sCalendar.set(Calendar.MILLISECOND, 0)

        return sCalendar.timeInMillis
    }

    /**
     * Gets the end of the year (Dec 31st 23:59:59.999) for a given timestamp.
     *
     * @param mills The timestamp.
     * @return The end of the year.
     */
    @Synchronized
    fun getEndOfYear(mills: Long): Long {
        sCalendar.timeInMillis = mills

        sCalendar.set(Calendar.MONTH, Calendar.DECEMBER)
        sCalendar.set(Calendar.DAY_OF_MONTH, 31)
        sCalendar.set(Calendar.HOUR_OF_DAY, 23)
        sCalendar.set(Calendar.MINUTE, 59)
        sCalendar.set(Calendar.SECOND, 59)
        sCalendar.set(Calendar.MILLISECOND, 999)

        return sCalendar.timeInMillis
    }

    /**
     * Gets the year component of a timestamp.
     *
     * @param mills The timestamp.
     * @return The year.
     */
    @Synchronized
    fun getYear(mills: Long): Int {
        sCalendar.timeInMillis = mills

        return sCalendar.get(Calendar.YEAR)
    }

    /**
     * Gets the month component of a timestamp.
     *
     * @param mills The timestamp.
     * @return The month (0-based).
     */
    @Synchronized
    fun getMonth(mills: Long): Int {
        sCalendar.timeInMillis = mills

        return sCalendar.get(Calendar.MONTH)
    }

    /**
     * Gets the week of year for a timestamp.
     *
     * @param mills The timestamp.
     * @return The week of year.
     */
    @Synchronized
    fun getWeek(mills: Long): Int {
        /* Week number according to the ISO-8601 standard,
		 * weeks starting on Monday. The first week of the
		 * year is the week that contains that year's first
		 * Thursday (='First 4-day week'). The highest week
		 *  number in a year is either 52 or 53.
		 */

        sCalendar.firstDayOfWeek = Calendar.MONDAY
        sCalendar.minimalDaysInFirstWeek = 4
        sCalendar.timeInMillis = mills

        return sCalendar.get(Calendar.WEEK_OF_YEAR)
    }

    /**
     * Gets the day of month for a timestamp.
     *
     * @param mills The timestamp.
     * @return The day of month.
     */
    @Synchronized
    fun getDay(mills: Long): Int {
        sCalendar.timeInMillis = mills

        return sCalendar.get(Calendar.DAY_OF_MONTH)
    }

    /**
     * Gets the day of week for a timestamp (1 = Monday, 7 = Sunday).
     *
     * @param mills The timestamp.
     * @return The day of week.
     */
    @Synchronized
    fun getWeekDay(mills: Long): Int {
        sCalendar.timeInMillis = mills

//        sCalendar.firstDayOfWeek = Calendar.MONDAY;

        var day = sCalendar.get(Calendar.DAY_OF_WEEK)
        day = if (day == 1) 7 else day - 1

        return day
    }

    /**
     * Gets the hour of day (24-hour) for a timestamp.
     *
     * @param mills The timestamp.
     * @return The hour.
     */
    @Synchronized
    fun getHour(mills: Long): Int {
        sCalendar.timeInMillis = mills

        return sCalendar.get(Calendar.HOUR_OF_DAY)
    }

    /**
     * Gets the minute for a timestamp.
     *
     * @param mills The timestamp.
     * @return The minute.
     */
    @Synchronized
    fun getMinute(mills: Long): Int {
        sCalendar.timeInMillis = mills

        return sCalendar.get(Calendar.MINUTE)
    }

    /**
     * Gets the second for a timestamp.
     *
     * @param mills The timestamp.
     * @return The second.
     */
    @Synchronized
    fun getSecond(mills: Long): Int {
        sCalendar.timeInMillis = mills

        return sCalendar.get(Calendar.SECOND)
    }

    /**
     * Checks if a timestamp is in the current day.
     *
     * @param time The timestamp.
     * @return True if in current day.
     */
    fun isCurrentDay(time: Long): Boolean {
        val now = System.currentTimeMillis()

        return getStartOfDay(time) == getStartOfDay(now)
    }

    /**
     * Checks if a timestamp is in the current week.
     *
     * @param time The timestamp.
     * @return True if in current week.
     */
    fun isCurrentWeek(time: Long): Boolean {
        val now = System.currentTimeMillis()

        return getStartOfWeek(time) == getStartOfWeek(now)
    }

    /**
     * Checks if a timestamp is in the current month.
     *
     * @param time The timestamp.
     * @return True if in current month.
     */
    fun isCurrentMonth(time: Long): Boolean {
        val now = System.currentTimeMillis()

        return getStartOfMonth(time) == getStartOfMonth(now)
    }

    /**
     * Checks if a timestamp is in the current year.
     *
     * @param time The timestamp.
     * @return True if in current year.
     */
    fun isCurrentYear(time: Long): Boolean {
        val now = System.currentTimeMillis()

        return getStartOfYear(time) == getStartOfYear(now)
    }

    /**
     * Checks if a timestamp is within a given range (inclusive).
     *
     * @param time The timestamp to check.
     * @param start The start of the range.
     * @param end The end of the range.
     * @return True if within range.
     */
    fun isInRange(time: Long, start: Long, end: Long): Boolean {
        return time in start..end
    }

    /**
     * Gets the current timezone offset in milliseconds.
     *
     * @return The offset.
     */
    fun getTimezoneOffset(): Long {
        val tz = TimeZone.getDefault()
        val now = Date()
        val offsetFromUtc = tz.getOffset(now.time)
        return offsetFromUtc.toLong()
    }

    /**
     * Formats a duration into a readable string (e.g., "1h 30' 15" 500").
     *
     * @param duration The duration in milliseconds.
     * @return The formatted string.
     */
    fun durationToReadableString(duration: Long): String? {
        val hourLabel = "h"
        val minLabel = "\'"
        val secLabel = "\""
        val sec: Long = duration / SECOND_IN_MILLIS
        val min: Long = duration / MINUTE_IN_MILLIS
        val hour: Long = duration / HOUR_IN_MILLIS
        return String.format("%d%s %02d%s %02d%s %03d",
                hour, hourLabel,
                min % 60, minLabel,
                sec % 60, secLabel,
                duration % 1000)
    }

    /**
     * Formats a timestamp into a readable date/time string.
     *
     * @param time The timestamp.
     * @param hasDate Whether to include the date.
     * @param hasTime Whether to include the time.
     * @return The formatted string.
     */
    fun timeToReadableString(time: Long,
                             hasDate: Boolean, hasTime: Boolean): String? {
        val builder = StringBuilder()
        if (hasDate) {
            builder.append("yyyy/MM/dd ")
        }
        if (hasTime) {
            builder.append("hh:mm:ss:SSS aa")
        }
        var format = builder.toString() ?: return null
        format = format.trim { it <= ' ' }
        val formater = SimpleDateFormat(format)
        return formater.format(time)
    }

    /**
     * Formats a timestamp into a readable date and time string.
     *
     * @param time The timestamp.
     * @return The formatted string.
     */
    fun timeToReadableString(time: Long): String? {
        return timeToReadableString(time, true, true)
    }

    /**
     * Formats a timestamp into a readable date string (no time).
     *
     * @param time The timestamp.
     * @return The formatted string.
     */
    fun timeToReadableStringWithoutTime(time: Long): String? {
        return timeToReadableString(time, true, false)
    }

    /**
     * Formats a timestamp into a readable time string (no date).
     *
     * @param time The timestamp.
     * @return The formatted string.
     */
    fun timeToReadableStringWithoutDate(time: Long): String? {
        return timeToReadableString(time, false, true)
    }

}