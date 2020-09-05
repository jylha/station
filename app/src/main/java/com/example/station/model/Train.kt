package com.example.station.model

import androidx.compose.runtime.Immutable
import com.example.station.util.atLocalZone
import java.time.LocalDateTime

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
        return arrivalAt(stationUicCode)?.scheduledTime?.atLocalZone()
    }

    /** Returns the scheduled time of departure from the specified station. */
    fun scheduledDepartureAt(stationUicCode: Int): LocalDateTime? {
        return departureAt(stationUicCode)?.scheduledTime?.atLocalZone()
    }

    private fun arrivalAt(stationUicCode: Int) = timetable.firstOrNull {
        it.stationUicCode == stationUicCode && it.type == TimetableRow.Type.Arrival
    }

    private fun departureAt(stationUicCode: Int) = timetable.firstOrNull {
        it.stationUicCode == stationUicCode && it.type == TimetableRow.Type.Departure
    }
}
