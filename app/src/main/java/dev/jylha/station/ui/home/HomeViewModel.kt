package dev.jylha.station.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jylha.station.data.settings.SettingsRepository
import dev.jylha.station.data.stations.StationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val SKIP_HOME_SCREEN_ENABLED: Boolean = false

class HomeViewModel @ViewModelInject constructor(
    private val settingsRepository: SettingsRepository,
    private val stationRepository: StationRepository
) : ViewModel() {
    private val mutex = Mutex()
    private val _state = MutableStateFlow(HomeViewState.initial())

    /** View model state. */
    val state: StateFlow<HomeViewState> = _state.asStateFlow()

    init {
        if (SKIP_HOME_SCREEN_ENABLED) {
            // When skipping home screen is enabled, most recently used station is loaded from the
            // application settings and its timetable is loaded and shown instead of home screen.
            viewModelScope.launch {
                reduceState(LoadSettings.Loading)
                settingsRepository.station().collect { stationCode ->
                    if (stationCode == null) {
                        reduceState(LoadSettings.Success(stationCode))
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

    private suspend fun reduceState(result: HomeViewResult) {
        mutex.withLock { _state.value = _state.value.reduce(result) }
    }
}


