package com.example.station.model

import androidx.compose.runtime.Immutable
import com.example.station.util.atLocalZone
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Domain Model for train information.
 * @param number Train number.
 * @param type Train type: IC, P, S...
 * @param category Train category.
 * @param isRunning Indicates whether train is currently running.
 * @param timetable Train's timetable.
 */
@Immutable
data class Train(
    val number: Int,
    val type: String,
    val category: Category,
    val isRunning: Boolean,
    val timetable: List<TimetableRow>
) {
    /** Train category. */
    sealed class Category {
        object LongDistance : Category()
        object Commuter: Category()
    }

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

    /** Checks whether train is marked ready on origin station. */
    fun isReady(): Boolean {
        return timetable.firstOrNull()?.markedReady == true
    }

    /** Checks whether train is not yet marked ready on origin station. */
    fun isNotReady(): Boolean = !isReady()

    /** Checks whether train is yet to arrive on the specified station. */
    fun onRouteTo(stationUicCode: Int): Boolean {
        val arrival = arrivalAt(stationUicCode)
        return (arrival != null && arrival.actualTime == null)
    }

    /** Checks whether train is currently on the specified station. */
    fun onStation(stationUicCode: Int): Boolean {
        return !hasDeparted(stationUicCode) &&
                (!isDestination(stationUicCode) || isRunning) &&
                (hasArrived(stationUicCode) || (isOrigin(stationUicCode) && isReady()))

    }

    /** Checks whether train has arrived to the specified station. */
    fun hasArrived(stationUicCode: Int): Boolean {
        return arrivalAt(stationUicCode)?.actualTime != null
    }

    /** Checks whether train has departed the specified station. */
    fun hasDeparted(stationUicCode: Int): Boolean {
        return departureAt(stationUicCode)?.actualTime != null
    }

    /** Checks whether the specified station is train's origin. */
    fun isOrigin(stationUicCode: Int): Boolean {
        return timetable.firstOrNull()?.stationUicCode == stationUicCode
    }

    /** Checks whether the specified station is train's destination. */
    fun isDestination(stationUicCode: Int): Boolean {
        return timetable.lastOrNull()?.stationUicCode == stationUicCode
    }

    /** Time of next scheduled event or most recent event on the specified station. */
    fun nextEvent(stationUicCode: Int): ZonedDateTime {
        val arrival = arrivalAt(stationUicCode)
        val departure = departureAt(stationUicCode)

        return when {
            departure?.actualTime != null -> departure.actualTime
            departure != null && (arrival == null || arrival.actualTime != null) -> departure.scheduledTime
            arrival?.actualTime != null -> arrival.actualTime
            arrival != null -> arrival.scheduledTime
            else -> ZonedDateTime.of(LocalDateTime.MIN, ZoneId.systemDefault())
        }
    }

    /** Returns a timetable row for the arrival to the specified station. */
    fun arrivalAt(stationUicCode: Int) = timetable.firstOrNull {
        it.stationUicCode == stationUicCode && it.type == TimetableRow.Type.Arrival
    }

    /** Returns a timetable row for the departure from the specified station. */
    fun departureAt(stationUicCode: Int) = timetable.firstOrNull {
        it.stationUicCode == stationUicCode && it.type == TimetableRow.Type.Departure
    }
}
