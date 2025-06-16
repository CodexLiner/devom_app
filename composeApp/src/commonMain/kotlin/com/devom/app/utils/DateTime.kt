package com.devom.app.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.LocalTime.Companion
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.toLocalTime
import kotlin.time.ExperimentalTime

fun LocalTime.to12HourTime(): String {
    val hour = if (this.hour % 12 == 0) 12 else this.hour % 12
    val minute = this.minute.toString().padStart(2, '0')
    val ampm = if (this.hour < 12) "AM" else "PM"
    return "$hour:$minute $ampm"
}

fun String.toTimeParts(): Pair<Int, Int> {
    return try {
        val localTime = parse12HourTime(this)
        localTime.hour to localTime.minute
    } catch (_: Exception) {
        0 to 0
    }
}

fun parse12HourTime(timeStr: String): LocalTime {
    val regex = Regex("""(\d{1,2}):(\d{2})\s*(AM|PM)""", RegexOption.IGNORE_CASE)
    val match = regex.matchEntire(timeStr) ?: throw IllegalArgumentException("Invalid time format")

    val (hourStr, minStr, ampm) = match.destructured
    var hour = hourStr.toInt()
    val minute = minStr.toInt()

    if (ampm.uppercase() == "PM" && hour != 12) hour += 12
    else if (ampm.uppercase() == "AM" && hour == 12) hour = 0

    return LocalTime(hour, minute)
}

fun LocalDateTime.addHours(hours: Int): LocalDateTime {
    return toInstant(TimeZone.currentSystemDefault()).plus(hours, DateTimeUnit.HOUR)
        .toLocalDateTime(TimeZone.currentSystemDefault())
}

fun LocalDateTime.addMinutes(minutes: Int): LocalDateTime {
    return toInstant(TimeZone.currentSystemDefault()).plus(minutes, DateTimeUnit.MINUTE)
        .toLocalDateTime(TimeZone.currentSystemDefault())
}

fun LocalDateTime.minusHours(hours: Int): LocalDateTime {
    return toInstant(TimeZone.currentSystemDefault()).minus(
        hours, DateTimeUnit.HOUR
    ).toLocalDateTime(TimeZone.currentSystemDefault())
}

fun String.to12HourTime(): String {
    return try {
        val time = LocalTime.parse(this)
        val hour = if (time.hour % 12 == 0) 12 else time.hour % 12
        val minute = time.minute.toString().padStart(2, '0')
        val amPm = if (time.hour < 12) "AM" else "PM"
        "$hour:$minute $amPm"
    } catch (e: Exception) {
        this
    }
}
fun String.to24HourTime(): String {
    val trimmed = this.trim()
    val twentyFourHourRegex = Regex("""^([01]?\d|2[0-3]):[0-5]\d$""")
    if (twentyFourHourRegex.matches(trimmed)) {
        return trimmed
    }

    val twelveHourRegex = Regex("""(\d{1,2}):(\d{2})\s*(AM|PM)""", RegexOption.IGNORE_CASE)
    val match = twelveHourRegex.matchEntire(trimmed)

    if (match != null) {
        val (hourStr, minuteStr, meridian) = match.destructured
        var hour = hourStr.toInt()

        if (meridian.equals("AM", ignoreCase = true)) {
            if (hour == 12) hour = 0
        } else if (meridian.equals("PM", ignoreCase = true)) {
            if (hour < 12) hour += 12
        }

        val hourFormatted = hour.toString().padStart(2, '0')
        val minuteFormatted = minuteStr.padStart(2, '0')

        return "$hourFormatted:$minuteFormatted"
    }

    // Return empty if not a recognizable time format
    return ""
}


fun LocalDate.format(pattern: String): String {
    return pattern
        .replace("yyyy", year.toString().padStart(4, '0'))
        .replace("MM", monthNumber.toString().padStart(2, '0'))
        .replace("dd", dayOfMonth.toString().padStart(2, '0'))
}

fun format12HourTime(hour: Int, minute: Int): String {
    val ampm = if (hour >= 12) "PM" else "AM"
    val hour12 = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    val minuteStr = minute.toString().padStart(2, '0')
    return "$hour12:$minuteStr $ampm"
}
