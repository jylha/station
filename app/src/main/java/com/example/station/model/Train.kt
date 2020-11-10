package com.example.station.model

import androidx.compose.runtime.Immutable
import com.example.station.model.TimetableRow.Type.Arrival
import com.example.station.model.TimetableRow.Type.Departure

/**
 * Domain Model for train information.
 * @param number Train number.
 * @param type Train type: IC, P, S...
 * @param category Train category.
 * @param commuterLineId Commuter line identifier.
 * @param isRunning Indicates whether train is currently running.
 * @param isCancelled Indicates whether train is cancelled.
 * @param version The number of version where the train data has last been changed.
 * @param timetable Train's timetable.
 */
@Immutable
data class Train(
    val number: Int,
    val type: String,
    val category: Category,
    val commuterLineId: String? = null,
    val isRunning: Boolean = true,
    val isCancelled: Boolean = false,
    val version: Long = 0,
    val timetable: List<TimetableRow> = emptyList()
) {
    /** Train category. */
    sealed class Category(val name: String) {
        object LongDistance : Category("Long-distance")
        object Commuter : Category("Commuter")

        override fun toString(): String = name
    }

    /** Returns the UIC code for the train's origin station. */
    fun origin(): Int? {
        return timetable.firstOrNull()?.stationCode
    }

    /** Returns the UIC code for the train's destination station. */
    fun destination(): Int? {
        return timetable.lastOrNull()?.stationCode
    }

    /** Returns the track for the given [stationCode]. */
    fun track(stationCode: Int): String? {
        return timetable.firstOrNull { it.stationCode == stationCode }?.track
    }

    /** Checks whether train is marked ready on origin station. */
    fun isReady(): Boolean {
        return timetable.firstOrNull()?.markedReady == true
    }

    /** Checks whether train is not yet marked ready on origin station. */
    fun isNotReady(): Boolean = !isReady()

    /** Checks whether train has reached its destination. */
    fun hasReachedDestination(): Boolean {
        return timetable.lastOrNull()?.actualTime != null
    }

    /** Checks whether the specified station is train's origin. */
    fun isOrigin(stationCode: Int): Boolean {
        return timetable.firstOrNull()?.stationCode == stationCode
    }

    /** Checks whether the specified station is train's destination. */
    fun isDestination(stationCode: Int): Boolean {
        return timetable.lastOrNull()?.stationCode == stationCode
    }

    override fun toString(): String = "Train(number=$number, type=$type, category=$category, ...)"
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
                if (first.type == Arrival && last.type == Departure && first.stationCode == last.stationCode) {
                    stops += Stop(first, last)
                }
            }
    }

    if (timetable.lastOrNull()?.type == Arrival) {
        stops += Stop(timetable.last())
    }
    return stops
}

/** Returns train's commercial stops. */
fun Train.commercialStops(): List<Stop> {
    return stops().filter { (arrival, departure) ->
        (arrival != null && arrival.trainStopping && arrival.commercialStop == true) ||
                (departure != null && departure.trainStopping && departure.commercialStop == true)
    }
}

/** Returns the trains current commercial stop. */
fun Train.currentCommercialStop(): Stop? {
    return commercialStops().findLast { (arrival, departure) ->
        arrival?.actualTime != null || departure?.actualTime != null || departure?.markedReady == true
    }
}

/** Returns train's stops at the specified station. */
fun Train.stopsAt(stationCode: Int): List<Stop> {
    return stops().filter { stop -> stop.stationCode() == stationCode }
}

/** Returns the causes for train's delay. */
fun Train.delayCauses(): List<DelayCause> {
    return timetable.map { it.causes }
        .fold(emptySet<DelayCause>()) { set, causes -> set + causes }
        .toList()
}

/** Returns whether train is a long-distance train. */
fun Train.isLongDistanceTrain(): Boolean {
    return category == Train.Category.LongDistance
}

/** Returns whether train is a commuter train. */
fun Train.isCommuterTrain(): Boolean {
    return category == Train.Category.Commuter
}
