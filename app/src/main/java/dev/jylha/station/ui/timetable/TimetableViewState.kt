package dev.jylha.station.ui.timetable

import androidx.compose.runtime.Immutable
import dev.jylha.station.data.stations.StationNameMapper
import dev.jylha.station.model.CauseCategories
import dev.jylha.station.model.Station
import dev.jylha.station.model.TimetableRow
import dev.jylha.station.model.Train
import timber.log.Timber

/**
 * The state of timetable screen.
 */
@Immutable
data class TimetableViewState(
    val isLoadingTimetable: Boolean = false,
    val loadingTimetableFailed: Boolean = false,
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
) {
    val isLoading: Boolean
        get() = isLoadingTimetable || isLoadingStationNames

    companion object {
        fun initial() = TimetableViewState(isLoadingTimetable = true)
    }
}

/** Reduce timetable state with given [TimetableResult]. */
fun TimetableViewState.reduce(result: TimetableResult): TimetableViewState {
    Timber.d("TimetableViewState.reduce(result = $result)")
    return when (result) {
        is LoadTimetable.Loading -> copy(
            isLoadingTimetable = true,
            loadingTimetableFailed = false,
            station = null,
            timetable = emptyList()
        )
        is LoadTimetable.Success -> copy(
            isLoadingTimetable = false,
            loadingTimetableFailed = false,
            station = result.station,
            timetable = result.timetable
        )
        is LoadTimetable.Error -> copy(
            isLoadingTimetable = false,
            loadingTimetableFailed = true,
            errorMessage = result.message,
            station = null,
            timetable = emptyList()
        )

        is SettingsUpdated -> copy(
            selectedTrainCategories = result.trainCategories ?: selectedTrainCategories,
            selectedTimetableTypes = result.timetableTypes ?: selectedTimetableTypes
        )

        LoadStationNames.Loading -> copy(
            isLoadingStationNames = true,
            stationNameMapper = null
        )
        is LoadStationNames.Error -> copy(
            isLoadingStationNames = false,
            errorMessage = result.message
        )
        is LoadStationNames.Success -> copy(
            isLoadingStationNames = false,
            stationNameMapper = result.stationNameMapper
        )

        ReloadTimetable.Loading -> copy(isReloadingTimetable = true)
        is ReloadTimetable.Error -> copy(
            isReloadingTimetable = false,
            errorMessage = result.message
        )
        is ReloadTimetable.Success -> copy(
            isReloadingTimetable = false,
            timetable = result.trains
        )

        LoadCauseCategories.Loading -> copy(isLoadingCauseCategories = true)
        is LoadCauseCategories.Error -> copy(
            isLoadingCauseCategories = false,
            errorMessage = result.message
        )
        is LoadCauseCategories.Success -> copy(
            isLoadingCauseCategories = false,
            causeCategories = result.categories
        )
    }
}
