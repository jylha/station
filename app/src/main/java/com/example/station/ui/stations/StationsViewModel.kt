package com.example.station.ui.stations

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dropbox.android.external.store4.StoreResponse
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

    private val _state = MutableStateFlow(StationsViewState.initial())

    /** A flow of view states. */
    val viewState: StateFlow<StationsViewState>
        get() = _state

    init {
        viewModelScope.launch {
            val mapper = stationRepository.getStationNameMapper()
            handle(StationsViewResult.NameMapper(mapper))
        }

        viewModelScope.launch {
            handle(StationsViewResult.LoadingStations)
            stationRepository.fetchStations().collect { response ->
                val result = when (response) {
                    is StoreResponse.Loading -> StationsViewResult.LoadingStations
                    is StoreResponse.Data -> StationsViewResult.StationsData(response.value)
                    is StoreResponse.NoNewData -> StationsViewResult.NoNewData
                    is StoreResponse.Error -> StationsViewResult.Error(response.errorMessageOrNull())
                }
                handle(result)
            }
        }

        viewModelScope.launch {
            settingsRepository.recentStations().collect { stations ->
                handle(StationsViewResult.RecentStations(stations))
            }
        }
    }

    fun stationSelected(station: Station) {
        viewModelScope.launch {
            settingsRepository.setStation(station.uic)
        }
    }

    private fun handle(result: StationsViewResult) {
        _state.value = _state.value.reduce(result)
    }
}
