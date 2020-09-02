package com.example.station.ui.timetable

import androidx.compose.runtime.Immutable
import com.example.station.model.Station
import com.example.station.model.Train

@Immutable
data class TimetableViewState(
    val loading: Boolean = true,
    val station: Station? = null,
    val timetable: List<Train>? = null,
    val error: String? = null
)

fun reduce(currentState: TimetableViewState, result: TimetableResult): TimetableViewState {
    return when (result) {
        is TimetableResult.Loading -> currentState.copy(
            loading = true,
            station = result.station,
            timetable = null
        )
        is TimetableResult.Success -> currentState.copy(
            loading = false,
            station = result.station,
            timetable = result.trains
        )
        is TimetableResult.Error -> currentState.copy(
            loading = false,
            error = result.msg
        )
    }
}