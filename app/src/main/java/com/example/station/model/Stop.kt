package com.example.station.model

import androidx.compose.runtime.Immutable
import java.lang.IllegalStateException
import java.time.ZonedDateTime

@Immutable
data class Stop(
    val arrival: TimetableRow? = null,
    val departure: TimetableRow? = null
) {
    init {
        require(arrival != null || departure != null)
        require(arrival == null || arrival.type == TimetableRow.Type.Arrival)
        require(departure == null || departure.type == TimetableRow.Type.Departure)
        require(arrival == null || departure == null || arrival.stationUic == departure.stationUic)
    }
}

fun Stop.isOrigin(): Boolean = arrival == null
fun Stop.isDestination(): Boolean = departure == null
fun Stop.isWaypoint(): Boolean = arrival != null && departure != null
fun Stop.stationUic(): Int = arrival?.stationUic ?: departure!!.stationUic
fun Stop.track(): String? = arrival?.track ?: departure?.track

/**
 * Returns the time of the next scheduled event on the stop, or the most recent event, if there
 * are no more scheduled events. */
fun Stop.timeOfNextEvent(): ZonedDateTime {
    return when {
        departure?.actualTime != null -> departure.actualTime
        departure != null && (arrival == null || arrival.actualTime != null) -> departure.scheduledTime
        arrival?.actualTime != null -> arrival.actualTime
        arrival != null -> arrival.scheduledTime
        else -> throw IllegalStateException()
    }
}
