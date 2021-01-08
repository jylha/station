package dev.jylha.station.testutil

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Test utility function to create ZonedDateTime object from short time string at local time zone.
 * @param time Local time in format HH:mm
 * @param date Date in form at yyyy-MM-dd
 */
fun at(time: String, date: String = "2020-01-01"): ZonedDateTime {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        .withZone(ZoneId.systemDefault())
    return ZonedDateTime.parse("$date $time", formatter)
}
