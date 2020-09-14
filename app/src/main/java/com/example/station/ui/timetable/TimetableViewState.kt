package com.example.station.ui.timetable

import androidx.compose.runtime.Immutable
import com.example.station.model.Station
import com.example.station.model.Train

@Immutable
data class TimetableViewState(
    val loading: Boolean = true,
    val station: Station? = null,
    val timetable: List<Train> = emptyList(),
    val selectedCategories: Set<Train.Category> =
        setOf(Train.Category.LongDistance, Train.Category.Commuter),
    val error: String? = null
)

fun reduce(currentState: TimetableViewState, result: TimetableResult): TimetableViewState {
    return when (result) {
        is TimetableResult.Loading -> currentState.copy(
            loading = true,
            station = result.station,
            timetable = emptyList()
        )
        is TimetableResult.Data -> currentState.copy(
            loading = false,
            station = result.station,
            timetable = result.trains
        )
        is TimetableResult.Error -> currentState.copy(
            loading = false,
            error = result.msg
        )
        is TimetableResult.SettingsUpdated -> currentState.copy(
            selectedCategories = result.categories
        )
    }
}
