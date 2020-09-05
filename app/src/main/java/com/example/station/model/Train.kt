package com.example.station.model

import androidx.compose.runtime.Immutable
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Domain Model for train information.
 * @param number Train number.
 * @param type Train type: IC, P, S...
 */
@Immutable
data class Train(
    val number: Int,
    val type: String,
    val timetable: List<TimetableRow>
) {
    /** Returns the station short code for the train's origin. */
    fun origin(): String? {
        return timetable.firstOrNull()?.stationCode
    }

    /** Returns the station short code for the train's destination. */
    fun destination(): String? {
        return timetable.lastOrNull()?.stationCode
    }

    /** Returns the track for the given [stationUicCode]. */
    fun track(stationUicCode: Int): String? {
        return timetable.firstOrNull { it.stationUicCode == stationUicCode }?.track
    }

    /** Returns the scheduled time of arrival to the specified station. */
    fun scheduledArrivalAt(stationUicCode: Int): LocalDateTime? {
        return timetable.firstOrNull { it.stationUicCode == stationUicCode}
            ?.scheduledTime?.withZoneSameInstant(ZoneId.systemDefault())?.toLocalDateTime()
    }
}