package com.example.station.ui.stations

import com.example.station.data.stations.StationNameMapper
import com.example.station.model.Station

sealed class StationsResult {

    /** This is used for clearing nearest station. */
    object SelectStation : StationsResult()

    object LoadingStations : StationsResult()
    object ReloadingStations : StationsResult()
    object NoNewData : StationsResult()
    data class StationsData(val stations: List<Station>) : StationsResult()
    data class RecentStations(val stations: List<Int>) : StationsResult()
    data class Error(val message: String?) : StationsResult()

    object LoadingNameMapper : StationsResult()
    data class NameMapper(val mapper: StationNameMapper) : StationsResult()

    object SelectNearest: StationsResult()

    data class Location(val latitude: Double, val longitude: Double): StationsResult()
    data class LocationError(val message: String?): StationsResult()
}
