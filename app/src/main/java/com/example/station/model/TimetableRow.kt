package com.example.station.model

import java.time.LocalDateTime


/**
 * Domain model for train's timetable row data.
 *
 * @param stationCode The short code for the station.
 * @param stationUicCode The UID code for the station.
 * @param track Track number.
 * @param scheduledTime The scheduled time for train's arrival or departure.
 */
data class TimetableRow(
    val stationCode: String,
    val stationUicCode: Int,
    val track: String,
    val scheduledTime: LocalDateTime
)