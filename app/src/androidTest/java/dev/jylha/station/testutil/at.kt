package dev.jylha.station.testutil

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.char
import kotlinx.datetime.toInstant

/**
 * Test utility function to create Instant object from short time string at local time zone.
 * @param time Local time in format HH:mm
 * @param date LocalDate
 */
fun at(time: String, date: LocalDate = LocalDate(2020, 1, 1)): Instant {
    val localTime = LocalTime.parse(time, LocalTime.Format { hour(); char(':'); minute() })
    return LocalDateTime(date, localTime).toInstant(TimeZone.currentSystemDefault())
}
