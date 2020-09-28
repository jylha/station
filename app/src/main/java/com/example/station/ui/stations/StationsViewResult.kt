package com.example.station.ui.stations

import com.example.station.data.stations.StationNameMapper
import com.example.station.model.Station

sealed class StationsViewResult {

    /** This is used for clearing nearest station. */
    object SelectStation : StationsViewResult()

    object LoadingStations : StationsViewResult()
    object NoNewData : StationsViewResult()
    data class StationsData(val stations: List<Station>) : StationsViewResult()
    data class NameMapper(val mapper: StationNameMapper) : StationsViewResult()
    data class RecentStations(val stations: List<Int>) : StationsViewResult()
    data class Error(val message: String?) : StationsViewResult()

    object SelectNearest: StationsViewResult()
    data class Location(val latitude: Double, val longitude: Double): StationsViewResult()
    data class LocationError(val message: String?): StationsViewResult()
}
