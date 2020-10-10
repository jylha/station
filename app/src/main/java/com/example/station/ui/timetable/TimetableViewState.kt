package com.example.station.ui.timetable

import androidx.compose.runtime.Immutable
import com.example.station.data.stations.StationNameMapper
import com.example.station.model.CauseCategories
import com.example.station.model.Station
import com.example.station.model.TimetableRow
import com.example.station.model.Train

@Immutable
data class TimetableViewState(
    val isLoadingTimetable: Boolean = false,
    val isReloadingTimetable: Boolean = false,
    val station: Station? = null,
    val timetable: List<Train> = emptyList(),
    val selectedTrainCategories: Set<Train.Category> =
        setOf(Train.Category.LongDistance, Train.Category.Commuter),
    val selectedTimetableTypes: Set<TimetableRow.Type> =
        setOf(TimetableRow.Type.Arrival, TimetableRow.Type.Departure),
    val errorMessage: String? = null,
    val isLoadingStationNames: Boolean = false,
    val stationNameMapper: StationNameMapper? = null,
    val isLoadingCauseCategories: Boolean = false,
    val causeCategories: CauseCategories? = null,
)

fun TimetableViewState.reduce(result: TimetableResult): TimetableViewState {
    return when (result) {
        is TimetableResult.Loading -> copy(
            isLoadingTimetable = true,
            station = result.station,
            timetable = emptyList()
        )
        is TimetableResult.Data -> copy(
            isLoadingTimetable = false,
            station = result.station,
            timetable = result.trains
        )
        is TimetableResult.Error -> copy(
            isLoadingTimetable = false,
            errorMessage = result.msg
        )
        is TimetableResult.SettingsUpdated -> copy(
            selectedTrainCategories = result.trainCategories ?: selectedTrainCategories,
            selectedTimetableTypes = result.timetableTypes ?: selectedTimetableTypes
        )
        TimetableResult.LoadingStationNames -> copy(isLoadingStationNames = true)
        is TimetableResult.StationNames -> copy(
            isLoadingStationNames = false,
            stationNameMapper = result.stationNameMapper
        )
        TimetableResult.Reloading -> copy(isReloadingTimetable = true)
        is TimetableResult.ReloadedData -> copy(
            isReloadingTimetable = false,
            timetable = result.trains
        )
        TimetableResult.LoadingCauseCategories -> copy(isLoadingCauseCategories = true)
        is TimetableResult.CauseCategoriesLoaded -> copy(
            isLoadingCauseCategories = false,
            causeCategories = result.categories
        )
    }
}
