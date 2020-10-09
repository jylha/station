package com.example.station.ui.stations

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dropbox.android.external.store4.StoreResponse
import com.example.station.data.location.LocationService
import com.example.station.data.settings.SettingsRepository
import com.example.station.data.stations.StationRepository
import com.example.station.model.Station
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class StationsViewModel @ViewModelInject constructor(
    private val stationRepository: StationRepository,
    private val settingsRepository: SettingsRepository,
    private val locationService: LocationService,
    @ApplicationContext val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(StationsViewState.initial())

    /** A flow of view states. */
    val state: StateFlow<StationsViewState>
        get() = _state

    init {
        viewModelScope.launch {
            reduceState(StationsResult.LoadingNameMapper)
            val mapper = stationRepository.getStationNameMapper()
            reduceState(StationsResult.NameMapper(mapper))
        }

        viewModelScope.launch {
            reduceState(StationsResult.LoadingStations)
            stationRepository.fetchStations().collect { response ->
                val result = when (response) {
                    is StoreResponse.Loading -> StationsResult.ReloadingStations
                    is StoreResponse.Data -> StationsResult.StationsData(response.value)
                    is StoreResponse.NoNewData -> StationsResult.NoNewData
                    is StoreResponse.Error -> StationsResult.Error(response.errorMessageOrNull())
                }
                reduceState(result)
            }
        }

        viewModelScope.launch {
            settingsRepository.recentStations().collect { stations ->
                reduceState(StationsResult.RecentStations(stations))
            }
        }
    }

    /** Sets whether station is selected manually from the list, or the nearest station. */
    fun setSelectionMode(selectNearestStation: Boolean) {
        if (selectNearestStation) {
            selectNearestStation()
        } else {
            selectStation()
        }
    }

    /** Notify of station selection to store it to recently selected stations. */
    fun stationSelected(station: Station) {
        viewModelScope.launch {
            settingsRepository.setStation(station.uic)
        }
    }

    private fun selectStation() {
        reduceState(StationsResult.SelectStation)
    }

    private fun selectNearestStation() {
        viewModelScope.launch {
            reduceState(StationsResult.SelectNearest)
            val location = locationService.currentLocation().first()
            reduceState(StationsResult.Location(location.latitude, location.longitude))
        }
    }

    private fun reduceState(result: StationsResult) {
        _state.value = _state.value.reduce(result)
    }
}

