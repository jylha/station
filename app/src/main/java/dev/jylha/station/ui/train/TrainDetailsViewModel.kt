package dev.jylha.station.ui.train

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jylha.station.data.stations.StationRepository
import dev.jylha.station.data.trains.TrainRepository
import dev.jylha.station.model.Train
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class TrainDetailsViewModel @ViewModelInject constructor(
    private val trainRepository: TrainRepository,
    private val stationRepository: StationRepository
) : ViewModel() {
    private val mutex = Mutex()
    private val _state = MutableStateFlow(TrainDetailsViewState.initial())

    /** View model state. */
    val state: StateFlow<TrainDetailsViewState> = _state.asStateFlow()

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

    /** Set a [train] for the train details view. */
    fun setTrain(train: Train) {
        viewModelScope.launch {
            reduceState(LoadTrainDetails.Success(train))
        }
    }

    /** Reload [train] details. */
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

    private suspend fun reduceState(result: TrainDetailsResult) {
        mutex.withLock { _state.value = _state.value.reduce(result) }
    }
}
