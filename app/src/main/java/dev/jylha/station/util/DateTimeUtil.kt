package dev.jylha.station.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.minutes

/**
 * Converts an [Instant] to a local time string using the system's default time zone.
 */
fun Instant.toLocalTimeString(): String {
    return toLocalDateTime(TimeZone.currentSystemDefault()).format(shortTimeFormat)
}

private val shortTimeFormat: DateTimeFormat<LocalDateTime> = LocalDateTime.Format {
    time(
        LocalTime.Format {
            hour(); char(':'); minute()
        }
    )
}

/**
 *  Checks whether the difference between two times is at least a minute.
 */
fun Instant.differsFrom(other: Instant): Boolean {
    val difference = (this - other).absoluteValue
    return difference >= 1.minutes
}

/**
 * Returns the current date.
 */
fun LocalDate.Companion.now() =
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
