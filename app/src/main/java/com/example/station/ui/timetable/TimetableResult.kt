package com.example.station.ui.timetable

import com.example.station.data.stations.StationNameMapper
import com.example.station.model.CauseCategory
import com.example.station.model.Station
import com.example.station.model.TimetableRow
import com.example.station.model.Train

sealed class TimetableResult {
    data class Loading(val station: Station) : TimetableResult()
    data class Data(val station: Station, val trains: List<Train>) : TimetableResult()
    data class Error(val msg: String) : TimetableResult()
    data class SettingsUpdated(
        val trainCategories: Set<Train.Category>?,
        val timetableTypes: Set<TimetableRow.Type>?
    ) : TimetableResult()

    object LoadingStationNames : TimetableResult()
    data class StationNames(val stationNameMapper: StationNameMapper) : TimetableResult()

    object Reloading : TimetableResult()
    data class ReloadedData(val trains: List<Train>) : TimetableResult()

    object LoadingCauseCategories : TimetableResult()
    data class CauseCategories(val categories: List<CauseCategory>) : TimetableResult()
}
