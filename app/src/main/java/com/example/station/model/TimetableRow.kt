package com.example.station.model

import java.time.ZonedDateTime

/**
 * Domain model for train's timetable row data.
 *
 * @param stationCode The short code for the station.
 * @param stationUicCode The UIC code for the station.
 * @param type Time table entry type (either Arrival or Departure).
 * @param track Track number.
 * @param scheduledTime The scheduled time for train's arrival or departure.
 * @param actualTime Actual time of of arrival or departure.
 * @param differenceInMinutes Difference between scheduled and actual time in minutes.
 * @param markedReady Train is marked ready to depart (used only on origin station).
 */
data class TimetableRow(
    val stationCode: String,
    val stationUicCode: Int,
    val type: Type,
    val track: String,
    val scheduledTime: ZonedDateTime,
    val actualTime: ZonedDateTime? = null,
    val differenceInMinutes: Int? = null,
    val markedReady: Boolean = false
) {
    sealed class Type {
        object Arrival : Type()
        object Departure : Type()
    }
}
