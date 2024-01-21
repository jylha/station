package dev.jylha.station.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import dev.jylha.station.model.TimetableRow.Type.Arrival
import dev.jylha.station.model.TimetableRow.Type.Departure
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * Domain Model for train information.
 * @param number Train number.
 * @param type Train type: IC, P, S...
 * @param category Train category.
 * @param commuterLineId Commuter line identifier.
 * @param isRunning Indicates whether train is currently running.
 * @param isCancelled Indicates whether train is cancelled.
 * @param version The number of version where the train data has last been changed.
 * @param departureDate Train's departure date.
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
    val departureDate: LocalDate = LocalDate.now(),
    val version: Long = 0,
    val timetable: List<TimetableRow> = emptyList()
) {
    /** Train category. */
    sealed class Category(val name: String) {
        object LongDistance : Category("Long-distance")
        object Commuter : Category("Commuter")

        override fun toString(): String = name
    }

    /** Train's departure date as a string. */
    val departureDateString: String
        @Stable get() = departureDate.format(DateTimeFormatter.ISO_LOCAL_DATE)

    /** Returns whether train is a long-distance train. */
    @Stable
    fun isLongDistanceTrain(): Boolean {
        return category == Category.LongDistance
    }

    /** Returns whether train is a commuter train. */
    @Stable
    fun isCommuterTrain(): Boolean {
        return category == Category.Commuter
    }

    /** Returns the UIC code for the train's origin station. */
    @Stable
    fun origin(): Int? {
        return timetable.firstOrNull()?.stationCode
    }

    /** Returns the UIC code for the train's destination station. */
    @Stable
    fun destination(): Int? {
        return timetable.lastOrNull()?.stationCode
    }

    /** Returns the track for the given [stationCode]. */
    @Stable
    fun track(stationCode: Int): String? {
        return timetable.firstOrNull { it.stationCode == stationCode }?.track
    }

    /** Checks whether train is marked ready on origin station. */
    @Stable
    fun isReady(): Boolean {
        return timetable.firstOrNull()?.markedReady == true
    }

    /** Checks whether train is not yet marked ready on origin station. */
    @Stable
    fun isNotReady(): Boolean = !isReady()

    /** Checks whether train has reached its destination. */
    @Stable
    fun hasReachedDestination(): Boolean {
        return timetable.lastOrNull()?.actualTime != null
    }

    /** Checks whether the specified station is train's origin. */
    @Stable
    fun isOrigin(stationCode: Int): Boolean {
        return timetable.firstOrNull()?.stationCode == stationCode
    }

    /** Checks whether the specified station is train's destination. */
    @Stable
    fun isDestination(stationCode: Int): Boolean {
        return timetable.lastOrNull()?.stationCode == stationCode
    }

    override fun toString(): String = "Train(number=$number, type=$type, category=$category, ...)"

    /** Returns all train's stops. */
    @Stable
    fun stops(): List<Stop> {
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
    @Stable
    fun commercialStops(): List<Stop> {
        return stops().filter { (arrival, departure) ->
            (arrival != null && arrival.trainStopping && arrival.commercialStop == true) ||
                    (departure != null && departure.trainStopping && departure.commercialStop == true)
        }
    }

    /** Returns the trains current commercial stop. */
    @Stable
    fun currentCommercialStop(): Stop? {
        return commercialStops().findLast { (arrival, departure) ->
            arrival?.actualTime != null || departure?.actualTime != null || departure?.markedReady == true
        }
    }

    /** Returns train's stops at the specified station. */
    @Stable
    fun stopsAt(stationCode: Int): List<Stop> {
        return stops().filter { stop -> stop.stationCode() == stationCode }
    }

    /** Returns the causes for train's delay. */
    @Stable
    fun delayCauses(): ImmutableList<DelayCause> {
        return timetable.map { it.causes }
            .fold(emptySet<DelayCause>()) { set, causes -> set + causes }
            .toImmutableList()
    }
}
