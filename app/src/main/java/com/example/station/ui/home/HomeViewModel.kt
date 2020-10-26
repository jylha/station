package com.example.station.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.station.data.settings.SettingsRepository
import com.example.station.data.stations.StationRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel @ViewModelInject constructor(
    private val settingsRepository: SettingsRepository,
    private val stationRepository: StationRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HomeViewState.initial())

    val state: StateFlow<HomeViewState>
        get() = _state

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

    private fun reduceState(result: HomeViewResult) {
        _state.value = _state.value.reduce(result)
    }
}


