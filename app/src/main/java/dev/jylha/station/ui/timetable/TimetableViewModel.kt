package dev.jylha.station.ui.timetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.jylha.station.data.settings.SettingsRepository
import dev.jylha.station.data.stations.StationRepository
import dev.jylha.station.data.trains.TrainRepository
import dev.jylha.station.di.IoDispatcher
import dev.jylha.station.model.CauseCategories
import dev.jylha.station.model.Station
import dev.jylha.station.model.TimetableRow
import dev.jylha.station.model.Train
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
@OptIn(FlowPreview::class)
class TimetableViewModel @Inject constructor(
    private val trainRepository: TrainRepository,
    private val stationRepository: StationRepository,
    private val settingsRepository: SettingsRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : ViewModel() {
    private val eventChannel = Channel<TimetableEvent>(Channel.UNLIMITED)
    private val mutex = Mutex()
    private val _state = MutableStateFlow(TimetableViewState.initial())

    /** View model state. */
    val state: StateFlow<TimetableViewState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            handleEvents()
        }

        viewModelScope.launch {
            withContext(dispatcher) {
                reduceState(LoadStationNames.Loading)
                try {
                    val mapper = stationRepository.getStationNameMapper()
                    reduceState(LoadStationNames.Success(mapper))
                } catch (e: Exception) {
                    reduceState(LoadStationNames.Error(e.message))
                }
            }
        }

        viewModelScope.launch {
            withContext(dispatcher) {
                combine(
                    settingsRepository.trainCategories(),
                    settingsRepository.timetableTypes()
                ) { trainCategories, timetableTypes ->
                    SettingsUpdated(trainCategories, timetableTypes)
                }.collect { result -> reduceState(result) }
            }
        }

        viewModelScope.launch {
            withContext(dispatcher) {
                reduceState(LoadCauseCategories.Loading)
                try {
                    val categories = async { trainRepository.causeCategories() }
                    val detailedCategories = async { trainRepository.detailedCauseCategories() }
                    val thirdLevelCategories = async { trainRepository.thirdLevelCauseCategories() }
                    val causeCategories = CauseCategories(
                        categories = categories.await(),
                        detailedCategories = detailedCategories.await(),
                        thirdLevelCategories = thirdLevelCategories.await()
                    )
                    reduceState(LoadCauseCategories.Success(causeCategories))
                } catch (e: Exception) {
                    reduceState(LoadCauseCategories.Error(e.message))
                }
            }
        }
    }

    /** Offer an [event] to be handled by the view model. */
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
            emit(LoadTimetable.Loading(station))
            try {
                val trains = trainRepository.trainsAtStation(station).first()
                emit(LoadTimetable.Success(trains))
            } catch (e: Exception) {
                emit(LoadTimetable.Error(e.message))
            }
        }
    }

    private fun reloadTimetable(station: Station): Flow<TimetableResult> {
        return flow {
            emit(ReloadTimetable.Loading)
            try {
                val trains = trainRepository.trainsAtStation(station).first()
                emit(ReloadTimetable.Success(trains))
            } catch (e: Exception) {
                emit(ReloadTimetable.Error(e.message))
            }
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

    private suspend fun reduceState(result: TimetableResult) {
        mutex.withLock { _state.value = _state.value.reduce(result) }
    }
}
