package com.example.station.ui.select

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.station.data.stations.StationsRepository

class SelectStationsViewModel(
    stationsRepository: StationsRepository = StationsRepository()
) : ViewModel() {

    val stations = stationsRepository.fetchStations().asLiveData()

    override fun onCleared() {
        super.onCleared()
    }
}