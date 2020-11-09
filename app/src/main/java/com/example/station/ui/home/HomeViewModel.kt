package com.example.station.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.station.data.settings.SettingsRepository
import com.example.station.data.stations.StationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class HomeViewModel @ViewModelInject constructor(
    private val settingsRepository: SettingsRepository,
    private val stationRepository: StationRepository
) : ViewModel() {
    private val mutex = Mutex()
    private val _state = MutableStateFlow(HomeViewState.initial())

    /** View model state. */
    val state: StateFlow<HomeViewState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            reduceState(LoadSettings.Loading)
            settingsRepository.station().collect { stationUicCode ->
                if (true || stationUicCode == null) {
                    reduceState(LoadSettings.Success(stationUicCode))
                } else {
                    reduceState(LoadStation.Loading)
                    try {
                        val station = stationRepository.fetchStation(stationUicCode)
                        reduceState(LoadStation.Success(station))
                    } catch (e: Exception) {
                        reduceState(LoadStation.Error(e.message))
                    }
                }
            }
        }
    }

    private suspend fun reduceState(result: HomeViewResult) {
        mutex.withLock { _state.value = _state.value.reduce(result) }
    }
}


