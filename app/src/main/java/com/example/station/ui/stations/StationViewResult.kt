package com.example.station.ui.stations

import com.example.station.data.stations.StationNameMapper

sealed class StationViewResult {
    data class NameMapper(val mapper: StationNameMapper) : StationViewResult()
    data class RecentStations(val stations: List<Int>) : StationViewResult()
}
