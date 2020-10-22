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
            reduceState(LoadNameMapper.Loading)
            try {
                val mapper = stationRepository.getStationNameMapper()
                reduceState(LoadNameMapper.Success(mapper))
            } catch (e: Exception) {
                reduceState(LoadNameMapper.Error(e.message))
            }
        }
    }

    fun setTrain(train: Train) {
        reduceState(LoadTrainDetails.Success(train))
    }

    fun reload(train: Train) {
        viewModelScope.launch {
            reduceState(ReloadTrainDetails.Loading)
            try {
                val reloaded = trainRepository.train(train.number, train.version)
                reduceState(ReloadTrainDetails.Success(reloaded ?: train))
            } catch (e: Exception) {
                reduceState(ReloadTrainDetails.Error(e.message))
            }
        }
    }

    private fun reduceState(result: TrainDetailsResult) {
        _state.value = _state.value.reduce(result)
    }
}
