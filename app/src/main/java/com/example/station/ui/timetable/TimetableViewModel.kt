package com.example.station.ui.timetable

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.station.data.settings.SettingsRepository
import com.example.station.data.stations.StationRepository
import com.example.station.data.trains.TrainRepository
import com.example.station.model.CauseCategories
import com.example.station.model.Station
import com.example.station.model.TimetableRow
import com.example.station.model.Train
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class TimetableViewModel @ViewModelInject constructor(
    private val trainRepository: TrainRepository,
    private val stationRepository: StationRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val eventChannel = Channel<TimetableEvent>(Channel.UNLIMITED)
    private val _state = MutableStateFlow(TimetableViewState())

    val state: StateFlow<TimetableViewState>
        get() = _state

    init {
        viewModelScope.launch {
            handleEvents()
        }

        viewModelScope.launch {
            reduceState(TimetableResult.LoadingStationNames)
            val mapper = stationRepository.getStationNameMapper()
            reduceState(TimetableResult.StationNames(mapper))
        }

        viewModelScope.launch {
            combine(
                settingsRepository.trainCategories(),
                settingsRepository.timetableTypes()
            ) { trainCategories, timetableTypes ->
                TimetableResult.SettingsUpdated(trainCategories, timetableTypes)
            }.collect { result -> reduceState(result) }
        }

        viewModelScope.launch {
            reduceState(TimetableResult.LoadingCauseCategories)
            val causeCategories = trainRepository.causeCategories()
            val detailedCauseCategories = trainRepository.detailedCauseCategories()
            val allCategories = CauseCategories(causeCategories, detailedCauseCategories)
            reduceState(TimetableResult.CauseCategoriesLoaded(allCategories))
        }
    }

    fun offer(event: TimetableEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }

    private suspend fun handleEvents() {
        eventChannel.consumeAsFlow()
            .flatMapMerge { event ->
                when (event) {
                    is TimetableEvent.LoadTimetable -> loadTimetable(event.station)
                    is TimetableEvent.SelectCategories -> setTrainCategories(event.categories)
                    is TimetableEvent.SelectTimetableTypes -> setTimetableTypes(event.types)
                    is TimetableEvent.ReloadTimetable -> reloadTimetable(event.station)
                }
            }
            .collect { result -> reduceState(result) }
    }

    private fun loadTimetable(station: Station): Flow<TimetableResult> {
        return flow {
            emit(TimetableResult.Loading(station))
            trainRepository.trainsAtStation(station)
                .catch { e -> emit(TimetableResult.Error(e.toString())) }
                .collect { trains -> emit(TimetableResult.Data(station, trains)) }
        }
    }

    private fun reloadTimetable(station: Station): Flow<TimetableResult> {
        return flow {
            emit(TimetableResult.Reloading)
            val trains = trainRepository.trainsAtStation(station)
                .catch { e -> emit(TimetableResult.Error(e.toString())) }
                .first()
            emit(TimetableResult.ReloadedData(trains))
        }
    }

    private fun setTrainCategories(trainCategories: Set<Train.Category>): Flow<TimetableResult> {
        return flow {
            settingsRepository.setTrainCategories(trainCategories)
        }
    }

    private fun setTimetableTypes(types: Set<TimetableRow.Type>): Flow<TimetableResult> {
        return flow {
            settingsRepository.setTimetableTypes(types)
        }
    }

    private fun reduceState(result: TimetableResult) {
        _state.value = _state.value.reduce(result)
    }
}
