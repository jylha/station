package com.example.station.ui.select

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.station.data.stations.StationsRepository

class SelectStationsViewModel @ViewModelInject constructor(
    stationsRepository: StationsRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val stations = stationsRepository.fetchStations().asLiveData()

    override fun onCleared() {
        super.onCleared()
    }
}