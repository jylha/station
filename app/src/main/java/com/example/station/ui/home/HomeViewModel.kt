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
import timber.log.Timber


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
            settingsRepository.station().collect { stationUicCode ->
                if (stationUicCode == null) {
                    Timber.d("uicCode: $stationUicCode")
                    _state.value = _state.value.copy(loading = false)
                } else {
                    val station = stationRepository.fetchStation(stationUicCode)
                    _state.value = _state.value.copy(loading = false, station = station)
                }
            }
        }
    }
}


