package com.example.station.ui.timetable

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.station.data.trains.TrainRepository
import com.example.station.model.Station
import com.example.station.model.Train
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class TimetableViewModel @ViewModelInject constructor(
    private val trainRepository: TrainRepository
) : ViewModel() {
    private val eventChannel = Channel<TimetableEvent>(Channel.UNLIMITED)
    private val _state = MutableStateFlow(TimetableViewState())

    val state: StateFlow<TimetableViewState>
        get() = _state

    init {
        viewModelScope.launch {
            handleEvents()
        }
    }

    fun offer(event: TimetableEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }

    @OptIn(FlowPreview::class)
    private suspend fun handleEvents() {
        eventChannel.consumeAsFlow()
            .flatMapMerge { event ->
                when (event) {
                    is TimetableEvent.LoadTimetable -> loadTimetable(event.station)
                }
            }
            .collect { result ->
                _state.value = reduce(_state.value, result)
            }
    }

    private fun loadTimetable(station: Station): Flow<TimetableResult> {
        return flow {
            emit(TimetableResult.Loading(station))
            trainRepository.fetchTrains(station.code)
                .catch { e -> emit(TimetableResult.Error(e.toString())) }
                .collect { trains ->
                    emit(TimetableResult.Success(station, trains))
                }
        }
    }
}

sealed class TimetableEvent {
    data class LoadTimetable(val station: Station) : TimetableEvent()
}

sealed class TimetableResult {
    data class Loading(val station: Station) : TimetableResult()
    data class Success(val station: Station, val trains: List<Train>) : TimetableResult()
    data class Error(val msg: String) : TimetableResult()
}
