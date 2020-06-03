package com.dailystudio.devbricksx.utils

import java.text.SimpleDateFormat
import java.util.*

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

    @Synchronized
    fun getTimeOfDay(mills: Long): Long {
        sCalendar.timeInMillis = mills
        sCalendar.set(Calendar.YEAR, 2013)
        sCalendar.set(Calendar.MONTH, 14)
        sCalendar.set(Calendar.DAY_OF_MONTH, 8)

        return sCalendar.timeInMillis
    }

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

    @Synchronized
    fun getStartOfDay(mills: Long): Long {
        sCalendar.timeInMillis = mills

        sCalendar.set(Calendar.HOUR_OF_DAY, 0)
        sCalendar.set(Calendar.MINUTE, 0)
        sCalendar.set(Calendar.SECOND, 0)
        sCalendar.set(Calendar.MILLISECOND, 0)

        return sCalendar.timeInMillis
    }

    @Synchronized
    fun getEndOfDay(mills: Long): Long {
        sCalendar.timeInMillis = mills

        sCalendar.set(Calendar.HOUR_OF_DAY, 23)
        sCalendar.set(Calendar.MINUTE, 59)
        sCalendar.set(Calendar.SECOND, 59)
        sCalendar.set(Calendar.MILLISECOND, 999)

        return sCalendar.timeInMillis
    }

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

    @Synchronized
    fun getYear(mills: Long): Int {
        sCalendar.timeInMillis = mills

        return sCalendar.get(Calendar.YEAR)
    }

    @Synchronized
    fun getMonth(mills: Long): Int {
        sCalendar.timeInMillis = mills

        return sCalendar.get(Calendar.MONTH)
    }

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

    @Synchronized
    fun getDay(mills: Long): Int {
        sCalendar.timeInMillis = mills

        return sCalendar.get(Calendar.DAY_OF_MONTH)
    }

    @Synchronized
    fun getWeekDay(mills: Long): Int {
        sCalendar.timeInMillis = mills

//        sCalendar.firstDayOfWeek = Calendar.MONDAY;

        var day = sCalendar.get(Calendar.DAY_OF_WEEK)
        day = if (day == 1) 7 else day - 1

        return day
    }

    @Synchronized
    fun getHour(mills: Long): Int {
        sCalendar.timeInMillis = mills

        return sCalendar.get(Calendar.HOUR_OF_DAY)
    }

    @Synchronized
    fun getMinute(mills: Long): Int {
        sCalendar.timeInMillis = mills

        return sCalendar.get(Calendar.MINUTE)
    }

    @Synchronized
    fun getSecond(mills: Long): Int {
        sCalendar.timeInMillis = mills

        return sCalendar.get(Calendar.SECOND)
    }

    fun isCurrentDay(time: Long): Boolean {
        val now = System.currentTimeMillis()

        return getStartOfDay(time) == getStartOfDay(now)
    }

    fun isCurrentWeek(time: Long): Boolean {
        val now = System.currentTimeMillis()

        return getStartOfWeek(time) == getStartOfWeek(now)
    }

    fun isCurrentMonth(time: Long): Boolean {
        val now = System.currentTimeMillis()

        return getStartOfMonth(time) == getStartOfMonth(now)
    }

    fun isCurrentYear(time: Long): Boolean {
        val now = System.currentTimeMillis()

        return getStartOfYear(time) == getStartOfYear(now)
    }

    fun isInRange(time: Long, start: Long, end: Long): Boolean {
        return time in start..end
    }

    fun getTimezoneOffset(): Long {
        val tz = TimeZone.getDefault()
        val now = Date()
        val offsetFromUtc = tz.getOffset(now.time)
        return offsetFromUtc.toLong()
    }

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

    fun timeToReadableString(time: Long): String? {
        return timeToReadableString(time, true, true)
    }

    fun timeToReadableStringWithoutTime(time: Long): String? {
        return timeToReadableString(time, true, false)
    }

    fun timeToReadableStringWithoutDate(time: Long): String? {
        return timeToReadableString(time, false, true)
    }

}