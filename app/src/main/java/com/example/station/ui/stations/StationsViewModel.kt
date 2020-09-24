package com.example.station.ui.stations

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.station.data.settings.SettingsRepository
import com.example.station.data.stations.StationRepository
import com.example.station.model.Station
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class StationsViewModel @ViewModelInject constructor(
    private val stationRepository: StationRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val state = MutableStateFlow(StationsViewState.initial())

    /** A flow of view states. */
    val viewState: StateFlow<StationsViewState>
        get() = state

    init {
        viewModelScope.launch {
            stationRepository.fetchStations().collect { response ->
                state.value = state.value.reduce(response)
            }
        }

        viewModelScope.launch {
            settingsRepository.recentStations().collect { stations ->
                state.value = state.value.reduce(StationViewResult.RecentStations(stations))
            }
        }

        viewModelScope.launch {
            val mapper = stationRepository.getStationNameMapper()
            state.value = state.value.reduce(StationViewResult.NameMapper(mapper))
        }
    }

    fun stationSelected(station: Station) {
        viewModelScope.launch {
            settingsRepository.setStation(station.uic)
        }
    }
}

