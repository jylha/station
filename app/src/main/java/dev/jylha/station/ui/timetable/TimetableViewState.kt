package dev.jylha.station.ui.timetable

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import dev.jylha.station.data.stations.StationNameMapper
import dev.jylha.station.model.CauseCategories
import dev.jylha.station.model.Station
import dev.jylha.station.model.TimetableRow
import dev.jylha.station.model.Train
import timber.log.Timber

/**
 * A UI state for the [TimetableScreen].
 *
 * @property isLoadingTimetable Whether timetable is being loaded.
 */
@Immutable
data class TimetableViewState(
    val isLoadingTimetable: Boolean = false,
    val loadingTimetableFailed: Boolean = false,
    val isReloadingTimetable: Boolean = false,
    val station: Station? = null,
    val timetable: List<Train> = emptyList(),
    val selectedTrainCategories: TrainCategories =
        TrainCategories(Train.Category.LongDistance, Train.Category.Commuter),
    val selectedTimetableTypes: TimetableTypes =
        TimetableTypes(TimetableRow.Type.Arrival, TimetableRow.Type.Departure),
    val errorMessage: String? = null,
    val isLoadingStationNames: Boolean = false,
    val stationNameMapper: StationNameMapper? = null,
    val isLoadingCauseCategories: Boolean = false,
    val causeCategories: CauseCategories? = null,
) {
    val isLoading: Boolean
        @Stable get() = isLoadingTimetable || isLoadingStationNames

    /** Reduce timetable state with given [TimetableResult]. */
    fun reduce(result: TimetableResult): TimetableViewState {
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
                selectedTrainCategories = if (result.trainCategories != null)
                    TrainCategories(result.trainCategories) else selectedTrainCategories,
                selectedTimetableTypes = if (result.timetableTypes != null)
                    TimetableTypes(result.timetableTypes) else selectedTimetableTypes
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

    companion object {
        val initial = TimetableViewState(isLoadingTimetable = true)
    }
}
