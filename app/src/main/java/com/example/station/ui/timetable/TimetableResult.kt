package com.example.station.ui.timetable

import com.example.station.data.stations.StationNameMapper
import com.example.station.model.Station
import com.example.station.model.Train

sealed class TimetableResult {
    data class Loading(val station: Station) : TimetableResult()
    data class Data(val station: Station, val trains: List<Train>) : TimetableResult()
    data class Error(val msg: String) : TimetableResult()
    data class SettingsUpdated(val categories: Set<Train.Category>) : TimetableResult()
    data class StationNames(val mapper: StationNameMapper) : TimetableResult()
    object Reloading : TimetableResult()
    data class ReloadedData(val trains: List<Train>) : TimetableResult()
}
