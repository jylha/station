package dev.jylha.station.ui.timetable

import dev.jylha.station.data.stations.StationNameMapper
import dev.jylha.station.model.CauseCategories
import dev.jylha.station.model.Station
import dev.jylha.station.model.TimetableRow
import dev.jylha.station.model.Train

/** Base class for all results on timetable screen. */
sealed class TimetableResult

/** Result for updated application preferences. */
data class SettingsUpdated(
    val trainCategories: Set<Train.Category>?,
    val timetableTypes: Set<TimetableRow.Type>?
) : TimetableResult()

/** Results for loading the timetable. */
sealed class LoadTimetable : TimetableResult() {
    object Loading : LoadTimetable() {
        override fun toString(): String = "LoadTimetable.Loading"
    }
    data class Error(val message: String?) : LoadTimetable()
    data class Success(val station: Station, val timetable: List<Train>) : LoadTimetable()
}

/** Results for reloading the timetable. */
sealed class ReloadTimetable : TimetableResult() {
    object Loading : ReloadTimetable() {
        override fun toString(): String = "ReloadTimetable.Loading"
    }
    data class Error(val message: String?) : ReloadTimetable()
    data class Success(val trains: List<Train>) : ReloadTimetable()
}

/** Results for loading station name mapper. */
sealed class LoadStationNames : TimetableResult() {
    object Loading : LoadStationNames() {
        override fun toString(): String = "LoadStationNames.Loading"
    }
    data class Error(val message: String?) : LoadStationNames()
    data class Success(val stationNameMapper: StationNameMapper) : LoadStationNames()
}

/** Results for loading cause categories for train delays. */
sealed class LoadCauseCategories : TimetableResult() {
    object Loading : LoadCauseCategories() {
        override fun toString(): String = "LoadCauseCategories.Loading"
    }
    data class Error(val message: String?) : LoadCauseCategories()
    data class Success(val categories: CauseCategories) : LoadCauseCategories()
}

