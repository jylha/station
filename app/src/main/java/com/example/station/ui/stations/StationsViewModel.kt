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
            reduceState(LoadNameMapper.Loading)
            try {
                val mapper = stationRepository.getStationNameMapper()
                reduceState(LoadNameMapper.Success(mapper))
            } catch (e: Exception) {
                reduceState(LoadNameMapper.Error(e.message))
            }
        }

        viewModelScope.launch {
            reduceState(LoadStations.Loading)
            stationRepository.fetchStations().collect { response ->
                val result = when (response) {
                    is StoreResponse.Loading -> LoadStations.Reloading
                    is StoreResponse.Data -> LoadStations.Success(response.value)
                    is StoreResponse.NoNewData -> LoadStations.NoNewData
                    is StoreResponse.Error -> LoadStations.Error(response.errorMessageOrNull())
                }
                reduceState(result)
            }
        }

        viewModelScope.launch {
            settingsRepository.recentStations().collect { stations ->
                reduceState(RecentStationsUpdated(stations))
            }
        }
    }

    /** Sets whether station is selected manually from the list, or the nearest station. */
    fun setSelectionMode(selectNearestStation: Boolean) {
        if (selectNearestStation) {
            selectNearestStation()
        } else {
            showStationList()
        }
    }

    /** Notify of station selection to add the station to the list of recently selected stations. */
    fun stationSelected(station: Station) {
        viewModelScope.launch {
            settingsRepository.setStation(station.uic)
        }
    }

    private fun showStationList() {
        reduceState(FetchLocation.Cancel)
    }

    private fun selectNearestStation() {
        viewModelScope.launch {
            reduceState(FetchLocation.Fetching)
            try {
                val location = locationService.currentLocation().first()
                reduceState(FetchLocation.Success(location.latitude, location.longitude))
            } catch (e: Exception) {
                reduceState(FetchLocation.Error(e.message))
            }
        }
    }

    private fun reduceState(result: StationsResult) {
        _state.value = _state.value.reduce(result)
    }
}

