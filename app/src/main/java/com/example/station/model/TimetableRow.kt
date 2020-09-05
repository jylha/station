package com.example.station.model

import java.time.ZonedDateTime


/**
 * Domain model for train's timetable row data.
 *
 * @param stationCode The short code for the station.
 * @param stationUicCode The UID code for the station.
 * @param type Time table entry type (either Arrival or Departure).
 * @param track Track number.
 * @param scheduledTime The scheduled time for train's arrival or departure.
 */
data class TimetableRow(
    val stationCode: String,
    val stationUicCode: Int,
    val type: Type,
    val track: String,
    val scheduledTime: ZonedDateTime
) {
    sealed class Type {
        object Arrival : Type()
        object Departure : Type()
    }
}