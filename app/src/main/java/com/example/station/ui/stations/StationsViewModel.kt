package com.example.station.ui.stations

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dropbox.android.external.store4.StoreResponse
import com.example.station.data.settings.SettingsRepository
import com.example.station.data.stations.StationRepository
import com.example.station.model.Station
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class StationsViewModel @ViewModelInject constructor(
    private val stationRepository: StationRepository,
    private val settingsRepository: SettingsRepository,
    @ApplicationContext val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(StationsViewState.initial())

    /** A flow of view states. */
    val state: StateFlow<StationsViewState>
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
        handle(StationsViewResult.SelectStation)
    }

    private fun selectNearestStation() {
        viewModelScope.launch {
            handle(StationsViewResult.SelectNearest)
            try {
                val fusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(context)
                fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                    if (task.isComplete && task.isSuccessful) {
                        val location = task.result
                        if (location != null) {
                            handle(
                                StationsViewResult.Location(
                                    location.latitude,
                                    location.longitude
                                )
                            )
                        } else {
                            handle(StationsViewResult.LocationError("Location not received."))
                        }
                    } else {
                        handle(StationsViewResult.LocationError(null))
                    }

                }
            } catch (e: SecurityException) {
                handle(StationsViewResult.LocationError("Security exception."))
            }
        }
    }

    private fun handle(result: StationsViewResult) {
        _state.value = _state.value.reduce(result)
    }
}
