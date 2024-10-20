package dev.jylha.station.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.datetime.Instant

/**
 * A data class for a train's stop at a stopping point. The stop consists of a [TimetableRow] for
 * eiter train's arrival, departure, or them both.
 */
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

    /** Checks whether the stop is train's origin. */
    @Stable
    fun isOrigin(): Boolean = arrival == null

    /** Checks whether the stop is train's destination. */
    @Stable
    fun isDestination(): Boolean = departure == null

    /** Checks whether the stop is a waypoint. */
    @Stable
    fun isWaypoint(): Boolean = arrival != null && departure != null

    @Stable
    fun stationCode(): Int = arrival?.stationCode ?: departure!!.stationCode

    @Stable
    fun track(): String? = arrival?.track ?: departure?.track

    @Stable
    fun isNotReached(): Boolean = arrival != null && arrival.actualTime == null

    @Stable
    fun isReached(): Boolean = arrival == null || arrival.actualTime != null

    @Stable
    fun isNotDeparted(): Boolean = departure != null && departure.actualTime == null

    @Stable
    fun isDeparted(): Boolean = departure?.actualTime != null

    /**
     * Returns the time of the next scheduled event on the stop, or the most recent event, if there
     * are no more scheduled events. */
    @Stable
    fun timeOfNextEvent(): Instant {
        return when {
            departure?.actualTime != null -> departure.actualTime
            departure != null && (arrival == null || arrival.actualTime != null) -> departure.scheduledTime
            arrival?.actualTime != null -> arrival.actualTime
            arrival != null -> arrival.scheduledTime
            else -> throw IllegalStateException()
        }
    }

    /** Checks whether the train either has not yet arrived to the stop or arrived after given time. */
    @Stable
    fun arrivalAfter(time: Instant): Boolean {
        return arrival?.run { actualTime?.isAfter(time) ?: true } ?: false
    }

    /** Checks whether the train has either not yet departed from the stop or departed after given time. */
    @Stable
    fun departureAfter(time: Instant): Boolean {
        return departure?.run { actualTime?.isAfter(time) ?: true } ?: false
    }
}

private fun Instant.isAfter(instant: Instant): Boolean {
    return toEpochMilliseconds() > instant.toEpochMilliseconds()
}
