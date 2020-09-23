package com.example.station.model

import androidx.compose.runtime.Immutable

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
