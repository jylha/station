package com.example.station.ui.timetable

import androidx.compose.runtime.Immutable
import com.example.station.data.stations.StationNameMapper
import com.example.station.model.Station
import com.example.station.model.TimetableRow
import com.example.station.model.Train

@Immutable
data class TimetableViewState(
    val loading: Boolean = false,
    val reloading: Boolean = false,
    val station: Station? = null,
    val timetable: List<Train> = emptyList(),
    val selectedTrainCategories: Set<Train.Category> =
        setOf(Train.Category.LongDistance, Train.Category.Commuter),
    val selectedTimetableTypes: Set<TimetableRow.Type> =
        setOf(TimetableRow.Type.Arrival, TimetableRow.Type.Departure),
    val error: String? = null,
    val mapper: StationNameMapper? = null
)

fun TimetableViewState.reduce(result: TimetableResult): TimetableViewState {
    return when (result) {
        is TimetableResult.Loading -> copy(
            loading = true,
            station = result.station,
            timetable = emptyList()
        )
        is TimetableResult.Data -> copy(
            loading = false,
            station = result.station,
            timetable = result.trains
        )
        is TimetableResult.Error -> copy(
            loading = false,
            error = result.msg
        )
        is TimetableResult.SettingsUpdated -> copy(
            selectedTrainCategories = result.trainCategories ?: selectedTrainCategories,
            selectedTimetableTypes = result.timetableTypes ?: selectedTimetableTypes
        )
        is TimetableResult.StationNames -> copy(
            mapper = result.mapper
        )
        TimetableResult.Reloading -> copy(reloading = true)
        is TimetableResult.ReloadedData -> copy(reloading = false)
    }
}
