package com.example.station.model

import androidx.compose.runtime.Immutable
import com.example.station.model.TimetableRow.Type.Arrival
import com.example.station.model.TimetableRow.Type.Departure
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
    sealed class Category(val name: String) {
        object LongDistance : Category("Long-distance")
        object Commuter : Category("Commuter")
    }

    /** Returns the station uic code for the train's origin. */
    fun origin(): Int? {
        return timetable.firstOrNull()?.stationUic
    }

    /** Returns the station uic code for the train's destination. */
    fun destination(): Int? {
        return timetable.lastOrNull()?.stationUic
    }

    /** Returns the track for the given [stationUic]. */
    fun track(stationUic: Int): String? {
        return timetable.firstOrNull { it.stationUic == stationUic }?.track
    }

    /** Returns the scheduled time of arrival to the specified station. */
    fun scheduledArrivalAt(stationUic: Int): LocalDateTime? {
        return arrivalAt(stationUic)?.scheduledTime?.atLocalZone()
    }

    /** Returns the scheduled time of departure from the specified station. */
    fun scheduledDepartureAt(stationUic: Int): LocalDateTime? {
        return departureAt(stationUic)?.scheduledTime?.atLocalZone()
    }

    /** Checks whether train is marked ready on origin station. */
    fun isReady(): Boolean {
        return timetable.firstOrNull()?.markedReady == true
    }

    /** Checks whether train is not yet marked ready on origin station. */
    fun isNotReady(): Boolean = !isReady()

    /** Checks whether train is yet to arrive on the specified station. */
    fun onRouteTo(stationUic: Int): Boolean {
        val arrival = arrivalAt(stationUic)
        return (arrival != null && arrival.actualTime == null)
    }

    /** Checks whether train is currently on the specified station. */
    fun onStation(stationUic: Int): Boolean {
        return !hasDeparted(stationUic) &&
                (!isDestination(stationUic) || isRunning) &&
                (hasArrived(stationUic) || (isOrigin(stationUic) && isReady()))
        // FIXME: 13.9.2020 This method incorrectly assumes that train can visit a station only once.
    }

    /** Checks whether train has arrived to the specified station. */
    fun hasArrived(stationUic: Int): Boolean {
        return arrivalAt(stationUic)?.actualTime != null
    }

    /** Checks whether train has departed the specified station. */
    fun hasDeparted(stationUic: Int): Boolean {
        return departureAt(stationUic)?.actualTime != null
    }

    /** Checks whether the specified station is train's origin. */
    fun isOrigin(stationUic: Int): Boolean {
        return timetable.firstOrNull()?.stationUic == stationUic
    }

    /** Checks whether the specified station is train's destination. */
    fun isDestination(stationUic: Int): Boolean {
        return timetable.lastOrNull()?.stationUic == stationUic
    }

    /** Time of next scheduled event or most recent event on the specified station. */
    fun nextEvent(stationUic: Int): ZonedDateTime {
        val arrival = arrivalAt(stationUic)
        val departure = departureAt(stationUic)

        return when {
            departure?.actualTime != null -> departure.actualTime
            departure != null && (arrival == null || arrival.actualTime != null) -> departure.scheduledTime
            arrival?.actualTime != null -> arrival.actualTime
            arrival != null -> arrival.scheduledTime
            else -> ZonedDateTime.of(LocalDateTime.MIN, ZoneId.systemDefault())
        }
    }

    /** Returns a timetable row for the arrival to the specified station. */
    fun arrivalAt(stationUic: Int) = timetable.firstOrNull {
        it.stationUic == stationUic && it.type == Arrival
    }

    /** Returns a timetable row for the departure from the specified station. */
    fun departureAt(stationUic: Int) = timetable.firstOrNull {
        it.stationUic == stationUic && it.type == Departure
    }
}

/** Returns all train's stops. */
fun Train.stops(): List<Stop> {
    val stops = mutableListOf<Stop>()

    if (timetable.firstOrNull()?.type == Departure) {
        stops += Stop(departure = timetable.first())
    }

    if (timetable.size > 2) {
        timetable
            .subList(1, timetable.lastIndex)
            .windowed(2, 2, false) { (first, last) ->
                if (first.type == Arrival && last.type == Departure && first.stationUic == last.stationUic) {
                    stops += Stop(first, last)
                }
            }
    }

    if (timetable.lastOrNull()?.type == Arrival) {
        stops += Stop(timetable.last())
    }
    return stops
}

/** Returns train's stops at the specified station. */
fun Train.stopsAt(stationUic: Int): List<Stop> {
    return stops().filter { stop -> stop.stationUic() == stationUic }
}
