package dev.jylha.station.ui.home

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import dev.jylha.station.model.Station

/**
 * A UI state for the [HomeScreen].
 *
 * @param isLoadingSettings Whether application settings are being loaded.
 * @param isLoadingStation Whether train stations is being loaded.
 * @param station Previously selected station, or null, if none was selected.
 */
@Immutable
data class HomeViewState(
    val isLoadingSettings: Boolean = false,
    val isLoadingStation: Boolean = false,
    val station: Station? = null
) {
    val isLoading: Boolean
        @Stable get() = isLoadingSettings || isLoadingStation

    fun reduce(result: HomeViewResult): HomeViewState {
        return when (result) {
            LoadSettings.Loading -> copy(isLoadingSettings = true)
            is LoadSettings.Error -> copy(isLoadingSettings = false)
            is LoadSettings.Success -> copy(isLoadingSettings = false)

            LoadStation.Loading -> copy(isLoadingStation = true, isLoadingSettings = false)
            is LoadStation.Error -> copy(isLoadingStation = false)
            is LoadStation.Success -> copy(isLoadingStation = false, station = result.station)
        }
    }

    companion object {
        val Initial: HomeViewState = HomeViewState()
    }
}
