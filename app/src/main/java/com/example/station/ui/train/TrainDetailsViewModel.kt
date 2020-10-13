package com.example.station.ui.train

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.station.data.stations.StationRepository
import com.example.station.data.trains.TrainRepository
import com.example.station.model.Train
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class TrainDetailsViewModel @ViewModelInject constructor(
    private val trainRepository: TrainRepository,
    private val stationRepository: StationRepository
) : ViewModel() {
    private val _state = MutableStateFlow(TrainDetailsViewState.initial())

    val state: StateFlow<TrainDetailsViewState>
        get() = _state

    init {
        viewModelScope.launch {
            reduceState(TrainDetailsResult.LoadingNameMapper)
            val mapper = stationRepository.getStationNameMapper()
            reduceState(TrainDetailsResult.NameMapper(mapper))
        }
    }

    fun setTrain(train: Train) {
        reduceState(TrainDetailsResult.TrainDetails(train))
    }

    fun reload(number: Int) {
        viewModelScope.launch {
            reduceState(TrainDetailsResult.ReloadingTrainDetails)
            val train = trainRepository.train(number).first()
            reduceState(TrainDetailsResult.TrainDetailsReloaded(train))
        }
    }

    private fun reduceState(result: TrainDetailsResult) {
        _state.value = _state.value.reduce(result)
    }
}
