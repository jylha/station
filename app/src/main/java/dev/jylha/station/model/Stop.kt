package dev.jylha.station.model

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
        require(arrival == null || departure == null || arrival.stationCode == departure.stationCode)
    }
}

fun Stop.isOrigin(): Boolean = arrival == null
fun Stop.isDestination(): Boolean = departure == null
fun Stop.isWaypoint(): Boolean = arrival != null && departure != null
fun Stop.stationCode(): Int = arrival?.stationCode ?: departure!!.stationCode
fun Stop.track(): String? = arrival?.track ?: departure?.track

fun Stop.isNotReached(): Boolean = arrival != null && arrival.actualTime == null
fun Stop.isReached(): Boolean = arrival == null || arrival.actualTime != null
fun Stop.isNotDeparted(): Boolean = departure != null && departure.actualTime == null
fun Stop.isDeparted(): Boolean = departure?.actualTime != null

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

/** Checks whether the train either has not yet arrived to the stop or arrived after given time. */
fun Stop.arrivalAfter(time: ZonedDateTime): Boolean {
    return arrival?.run { actualTime?.isAfter(time) ?: true } ?: false
}

/** Checks whether the train has either not yet departed from the stop or departed after given time. */
fun Stop.departureAfter(time: ZonedDateTime): Boolean {
    return departure?.run { actualTime?.isAfter(time) ?: true } ?: false
}
