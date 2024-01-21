package dev.jylha.station.ui.train

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.jylha.station.data.stations.StationRepository
import dev.jylha.station.data.trains.TrainRepository
import dev.jylha.station.model.Train
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Stable
@HiltViewModel
class TrainDetailsViewModel @Inject constructor(
    private val trainRepository: TrainRepository,
    private val stationRepository: StationRepository
) : ViewModel() {
    private val _state = MutableStateFlow(TrainDetailsViewState.initial)

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

    /** Set a [Train] for the train details view. */
    fun setTrain(departureDate: String, trainNumber: Int) {
        viewModelScope.launch {
            reduceState(LoadTrainDetails.Loading)
            try {
                trainRepository.train(departureDate, trainNumber)?.let { train ->
                    reduceState(LoadTrainDetails.Success(train))
                } ?: throw IllegalStateException("Train not found")

            } catch (e: Exception) {
                reduceState(LoadTrainDetails.Error(e.message))
            }
        }
    }

    /** Reload [Train] details. */
    fun reload(train: Train) {
        viewModelScope.launch {
            reduceState(ReloadTrainDetails.Loading)
            try {
                val departureDate = train.departureDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                val reloaded = trainRepository.train(departureDate, train.number, train.version)
                reduceState(ReloadTrainDetails.Success(reloaded ?: train))
            } catch (e: Exception) {
                reduceState(ReloadTrainDetails.Error(e.message))
            }
        }
    }

    private fun reduceState(result: TrainDetailsResult) {
        _state.update { state -> state.reduce(result) }
    }
}
