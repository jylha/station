package com.example.station.ui.stations

import com.example.station.data.stations.StationNameMapper
import com.example.station.model.Station

sealed class StationsViewResult {
    object LoadingStations : StationsViewResult()
    object NoNewData : StationsViewResult()
    data class StationsData(val stations: List<Station>) : StationsViewResult()
    data class NameMapper(val mapper: StationNameMapper) : StationsViewResult()
    data class RecentStations(val stations: List<Int>) : StationsViewResult()
    data class Error(val message: String?) : StationsViewResult()
}
