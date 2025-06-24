package com.devom.app.utils

import androidx.compose.ui.graphics.Color
import com.devom.app.DOCUMENT_BASE_URL
import com.devom.app.IMAGE_BASE_URL
import com.devom.models.slots.Slot
import com.devom.network.NetworkClient
import io.ktor.http.encodeURLPath
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

fun String.toColor(): Color {
    val hex = removePrefix("#")
    return Color(hex.toLong(16) or (if (hex.length == 6) 0xFF000000 else 0x00000000))
}

fun String?.toDevomImage(): String? {
    val encodedPath = this?.encodeURLPath()
    return IMAGE_BASE_URL + encodedPath
}

fun String?.toDevomDocument(): String? {
    val encodedPath = this?.encodeURLPath()
    return DOCUMENT_BASE_URL + encodedPath
}

fun String?.toRupay(): String {
    return "₹$this"
}



fun List<Slot>.updateSlotTimeAndShiftFollowingSlots(
    indexToUpdate: Int,
    newStartTime: String? = null,
    newEndTime: String? = null,
): MutableList<Slot> {
    val updatedSlots = this.map { it.copy() }.toMutableList()
    val slot = updatedSlots[indexToUpdate]
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    // Parse original times
    val originalStart = parse12HourTime(slot.startTime)
    val originalEnd = parse12HourTime(slot.endTime)

    // Apply changes
    val startTime = newStartTime?.let { parse12HourTime(it) } ?: originalStart
    val endTime = newEndTime?.let { parse12HourTime(it) } ?: originalEnd

    // Convert to LocalDateTime
    val startDT = today.atTime(startTime)
    var endDT = today.atTime(endTime)

    // Calculate gap in minutes
    val gapInMinutes = endDT.toInstant(TimeZone.currentSystemDefault())
        .minus(startDT.toInstant(TimeZone.currentSystemDefault()), DateTimeUnit.MINUTE)

    // If gap less than 2 hours (120 mins), update endDT to startDT + 2 hours
    if (gapInMinutes < 120) {
        endDT = startDT.addHours(2)
    }

    // Update the selected slot
    slot.startTime = startDT.time.to12HourTime()
    slot.endTime = endDT.time.to12HourTime()

    // Start shifting from updated end time
    var nextStart = endDT
    val slotGapHours = 2

    for (i in indexToUpdate + 1 until updatedSlots.size) {
        val s = updatedSlots[i]
        val newStartDT = nextStart
        val newEndDT = newStartDT.addHours(slotGapHours)

        s.startTime = newStartDT.time.to12HourTime()
        s.endTime = newEndDT.time.to12HourTime()

        nextStart = newEndDT
    }

    return updatedSlots
}
fun String.formatStatus(): String =
    this.lowercase()
        .replace("_", " ")
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

val videoExtensions = listOf("mp4", "mov", "avi", "mkv", "webm", "flv", "wmv", "3gp", "mpeg")


/**
 * Percent-encodes a string according to RFC 3986 for use in URLs.
 * Safe characters: ALPHA / DIGIT / "-" / "." / "_" / "~"
 */
fun String.urlEncode(): String {
    return buildString {
        val bytes = this@urlEncode.encodeToByteArray() // UTF‑8
        for (b in bytes) {
            val ch = b.toInt() and 0xFF
            when {
                ch in 'a'.code..'z'.code ||
                        ch in 'A'.code..'Z'.code ||
                        ch in '0'.code..'9'.code ||
                        ch == '-'.code || ch == '.'.code ||
                        ch == '_'.code || ch == '~'.code -> append(ch.toChar())
                else -> append("%" + ch.toString(16).uppercase().padStart(2, '0'))
            }
        }
    }
}

/**
 * Decodes percent-encoded UTF‑8 strings.
 * Converts '%HH' to corresponding byte and then to characters.
 */
fun String.urlDecode(): String {
    val bytes = mutableListOf<Byte>()
    var i = 0
    while (i < length) {
        when (val c = this[i]) {
            '%' -> {
                val hex = substring(i + 1, i + 3)
                val b = hex.toInt(16).toByte()
                bytes.add(b)
                i += 3
            }
            else -> {
                bytes.add(c.code.toByte())
                i++
            }
        }
    }
    return bytes.toByteArray().decodeToString()
}


inline fun <reified T> T.toJsonString(): String {
    return NetworkClient.config.jsonConfig.encodeToString(this)
}
inline fun <reified T> String.decodeFromString(): T {
    return NetworkClient.config.jsonConfig.decodeFromString(this)
}