package dev.jylha.station.ui.timetable

import com.google.common.truth.Truth.assertThat
import dev.jylha.station.data.settings.SettingsRepository
import dev.jylha.station.data.stations.StationNameMapper
import dev.jylha.station.data.stations.StationRepository
import dev.jylha.station.data.trains.TrainRepository
import dev.jylha.station.model.CauseCategories
import dev.jylha.station.model.Station
import dev.jylha.station.model.TimetableRow
import dev.jylha.station.model.Train
import dev.jylha.station.testutil.CoroutineScopeRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mockito.`when` as whenCalled

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class TimetableViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    @get:Rule val coroutineRule = CoroutineScopeRule(dispatcher)

    @Mock private lateinit var trainRepository: TrainRepository
    @Mock private lateinit var stationRepository: StationRepository
    @Mock private lateinit var settingsRepository: SettingsRepository

    private lateinit var viewModel: TimetableViewModel

    private val testMapper = StationNameMapper { stationCode ->
        when (stationCode) {
            1 -> "Helsinki"
            else -> null
        }
    }

    private val timetable1 = listOf(
        Train(1, "IC", Train.Category.LongDistance),
        Train(2, "S", Train.Category.LongDistance)
    )

    private val timetable2 = listOf(
        Train(1, "IC", Train.Category.LongDistance),
        Train(2, "S", Train.Category.LongDistance),
        Train(3, "A", Train.Category.Commuter)
    )

    private val timetableTypeFlow = MutableStateFlow<Set<TimetableRow.Type>?>(
        setOf(TimetableRow.Type.Arrival, TimetableRow.Type.Departure)
    )
    private val trainCategoryFlow = MutableStateFlow<Set<Train.Category>?>(
        setOf(Train.Category.LongDistance, Train.Category.Commuter)
    )

    @Before fun setup() = runTest(dispatcher) {
        whenCalled(trainRepository.causeCategories()).thenReturn(emptyList())
        whenCalled(trainRepository.detailedCauseCategories()).thenReturn(emptyList())
        whenCalled(trainRepository.thirdLevelCauseCategories()).thenReturn(emptyList())
        whenCalled(stationRepository.getStationNameMapper()).thenReturn(testMapper)
        whenCalled(settingsRepository.timetableTypes()).thenReturn(timetableTypeFlow)
        whenCalled(settingsRepository.trainCategories()).thenReturn(trainCategoryFlow)

        viewModel = TimetableViewModel(
            trainRepository,
            stationRepository,
            settingsRepository,
            dispatcher
        )
    }

    @Test fun `initial state`() = runTest(dispatcher) {
        val expected = TimetableViewState(
            isLoadingTimetable = true,
            stationNameMapper = testMapper,
            causeCategories = CauseCategories(emptyList(), emptyList(), emptyList())
        )
        val result = viewModel.state.value
        assertThat(result).isEqualTo(expected)
    }

    @Test fun `timetable type is changed to Arrival`() = runTest(dispatcher) {
        timetableTypeFlow.value = setOf(TimetableRow.Type.Arrival)
        val result = viewModel.state.value
        assertThat(result.selectedTimetableTypes).isEqualTo(setOf(TimetableRow.Type.Arrival))
    }

    @Test fun `timetable type is changed to Departure`() = runTest(dispatcher) {
        timetableTypeFlow.value = setOf(TimetableRow.Type.Departure)
        val result = viewModel.state.value
        assertThat(result.selectedTimetableTypes).isEqualTo(setOf(TimetableRow.Type.Departure))
    }

    @Test fun `train category is changed to LongDistance`() = runTest(dispatcher) {
        trainCategoryFlow.value = setOf(Train.Category.LongDistance)
        val result = viewModel.state.value
        assertThat(result.selectedTrainCategories).isEqualTo(setOf(Train.Category.LongDistance))
    }

    @Test fun `train category is changed to Commuter`() = runTest(dispatcher) {
        trainCategoryFlow.value = setOf(Train.Category.Commuter)
        val result = viewModel.state.value
        assertThat(result.selectedTrainCategories).isEqualTo(setOf(Train.Category.Commuter))
    }

    @Test fun `handle LoadTimetable event`() = runTest(dispatcher) {
        val station = Station("Helsinki", "HKI", 1, 10.0, 10.0)
        val trainChannel = Channel<List<Train>>()
        whenCalled(stationRepository.fetchStation(station.code)).thenReturn(station)
        whenCalled(trainRepository.trainsAtStation(station.shortCode)).thenReturn(trainChannel.receiveAsFlow())
        viewModel.offer(TimetableEvent.LoadTimetable(station.code))

        with(viewModel.state.value) {
            assertThat(isLoadingTimetable).isTrue()
            assertThat(timetable).isEmpty()
        }

        trainChannel.send(timetable1)

        with(viewModel.state.value) {
            assertThat(isLoadingTimetable).isFalse()
            assertThat(timetable).isEqualTo(timetable1)
        }
    }

    @Test fun `handle ReloadTimetable event`() = runTest(dispatcher) {
        val station = Station("Helsinki", "HKI", 1, 10.0, 10.0)
        val trainChannel = Channel<List<Train>>()
        whenCalled(stationRepository.fetchStation(station.code)).thenReturn(station)
        whenCalled(trainRepository.trainsAtStation(station.shortCode))
            .thenReturn(trainChannel.receiveAsFlow())
        viewModel.offer(TimetableEvent.LoadTimetable(station.code))
        trainChannel.send(timetable1)

        viewModel.offer(TimetableEvent.ReloadTimetable(station))

        with(viewModel.state.value) {
            assertThat(isLoadingTimetable).isFalse()
            assertThat(isReloadingTimetable).isTrue()
            assertThat(timetable).isEqualTo(timetable1)
        }

        trainChannel.send(timetable2)

        with(viewModel.state.value) {
            assertThat(isReloadingTimetable).isFalse()
            assertThat(timetable).isEqualTo(timetable2)
        }
    }

    @Test fun `handle SelectCategories event`() = runTest(dispatcher) {
        viewModel.offer(TimetableEvent.SelectCategories(TrainCategories(Train.Category.Commuter)))
        verify(settingsRepository).setTrainCategories(TrainCategories(Train.Category.Commuter))
    }

    @Test fun `handle SelectTimetableTypes event`() = runTest(dispatcher) {
        viewModel.offer(TimetableEvent.SelectTimetableTypes(TimetableTypes(TimetableRow.Type.Departure)))
        verify(settingsRepository).setTimetableTypes(TimetableTypes(TimetableRow.Type.Departure))
    }
}
