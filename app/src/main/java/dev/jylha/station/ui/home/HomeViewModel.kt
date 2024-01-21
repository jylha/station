package dev.jylha.station.ui.home

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.jylha.station.BuildConfig
import dev.jylha.station.domain.SettingsRepository
import dev.jylha.station.domain.StationRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * A view model for the [HomeScreen].
 *
 * @param settingsRepository A repository of application settings.
 * @param stationRepository A repository of train stations.
 */
@Stable
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val stationRepository: StationRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HomeViewState.Initial)

    /** View model state. */
    val state: StateFlow<HomeViewState> = _state.asStateFlow()

    init {
        if (BuildConfig.SKIP_HOME_SCREEN) {
            // When skipping home screen is enabled, most recently used station is loaded from the
            // application settings and its timetable is loaded and shown instead of home screen.
            viewModelScope.launch {
                reduceState(LoadSettings.Loading)
                settingsRepository.station().collect { stationCode ->
                    if (stationCode == null) {
                        reduceState(LoadSettings.Success)
                    } else {
                        reduceState(LoadStation.Loading)
                        try {
                            val station = stationRepository.fetchStation(stationCode)
                            reduceState(LoadStation.Success(station))
                        } catch (e: Exception) {
                            reduceState(LoadStation.Error(e.message))
                        }
                    }
                }
            }
        }
    }

    private fun reduceState(result: HomeViewResult) {
        _state.update { state -> state.reduce(result) }
    }
}


