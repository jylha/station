package dev.jylha.station.ui.stations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dropbox.android.external.store4.StoreResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.jylha.station.data.settings.SettingsRepository
import dev.jylha.station.data.stations.StationRepository
import dev.jylha.station.domain.GetLocationUseCase
import dev.jylha.station.model.Station
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.update

/** A view model for the [StationsScreen]. */
@HiltViewModel
class StationsViewModel @Inject constructor(
    private val stationRepository: StationRepository,
    private val settingsRepository: SettingsRepository,
    private val getLocation: GetLocationUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(StationsViewState())
    val state: StateFlow<StationsViewState> = _state.asStateFlow()

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
            settingsRepository.setStation(station.code)
        }
    }

    private fun showStationList() {
        reduceState(FetchLocation.Cancel)
    }

    private fun selectNearestStation() {
        viewModelScope.launch {
            reduceState(FetchLocation.Fetching)
            try {
                val location = getLocation()
                reduceState(FetchLocation.Success(location.latitude, location.longitude))
            } catch (e: Exception) {
                reduceState(FetchLocation.Error(e.message))
            }
        }
    }

    private fun reduceState(result: StationsResult) {
        _state.update { state -> state.reduce(result) }
    }
}

