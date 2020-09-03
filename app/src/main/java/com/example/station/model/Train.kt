package com.example.station.model

import androidx.compose.runtime.Immutable

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
}