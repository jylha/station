package com.example.station.ui.timetable

import com.example.station.data.settings.SettingsRepository
import com.example.station.data.stations.StationNameMapper
import com.example.station.data.stations.StationRepository
import com.example.station.data.trains.TrainRepository
import com.example.station.model.CauseCategories
import com.example.station.model.Station
import com.example.station.model.TimetableRow
import com.example.station.model.Train
import com.example.station.testutil.MainCoroutineScopeRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.test.runBlockingTest
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

    @get:Rule val coroutineRule: MainCoroutineScopeRule = MainCoroutineScopeRule()

    @Mock private lateinit var trainRepository: TrainRepository
    @Mock private lateinit var stationRepository: StationRepository
    @Mock private lateinit var settingsRepository: SettingsRepository

    private lateinit var viewModel: TimetableViewModel

    private val testMapper = StationNameMapper { stationUic ->
        when (stationUic) {
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

    @Before fun setup() = coroutineRule.runBlockingTest {
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
            coroutineRule.dispatcher
        )
    }

    @Test fun `initial state`() = coroutineRule.runBlockingTest {
        setup()
        val expected = TimetableViewState(
            isLoadingTimetable = true,
            stationNameMapper = testMapper,
            causeCategories = CauseCategories(emptyList(), emptyList(), emptyList())
        )
        val result = viewModel.state.value
        assertThat(result).isEqualTo(expected)
    }

    @Test fun `timetable type is changed to Arrival`() = coroutineRule.runBlockingTest {
        timetableTypeFlow.value = setOf(TimetableRow.Type.Arrival)
        val result = viewModel.state.value
        assertThat(result.selectedTimetableTypes).isEqualTo(setOf(TimetableRow.Type.Arrival))
    }

    @Test fun `timetable type is changed to Departure`() = coroutineRule.runBlockingTest {
        timetableTypeFlow.value = setOf(TimetableRow.Type.Departure)
        val result = viewModel.state.value
        assertThat(result.selectedTimetableTypes).isEqualTo(setOf(TimetableRow.Type.Departure))
    }

    @Test fun `train category is changed to LongDistance`() = coroutineRule.runBlockingTest {
        trainCategoryFlow.value = setOf(Train.Category.LongDistance)
        val result = viewModel.state.value
        assertThat(result.selectedTrainCategories).isEqualTo(setOf(Train.Category.LongDistance))
    }

    @Test fun `train category is changed to Commuter`() = coroutineRule.runBlockingTest {
        trainCategoryFlow.value = setOf(Train.Category.Commuter)
        val result = viewModel.state.value
        assertThat(result.selectedTrainCategories).isEqualTo(setOf(Train.Category.Commuter))
    }

    @Test fun `handle LoadTimetable event`() = coroutineRule.runBlockingTest {
        val station = Station("Helsinki", "HKI", 1, 10.0, 10.0)
        val trainChannel = Channel<List<Train>>()
        whenCalled(trainRepository.trainsAtStation(station)).thenReturn(trainChannel.receiveAsFlow())
        viewModel.offer(TimetableEvent.LoadTimetable(station))

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

    @Test fun `handle ReloadTimetable event`() = coroutineRule.runBlockingTest {
        val station = Station("Helsinki", "HKI", 1, 10.0, 10.0)
        val trainChannel = Channel<List<Train>>()
        whenCalled(trainRepository.trainsAtStation(station)).thenReturn(trainChannel.receiveAsFlow())
        viewModel.offer(TimetableEvent.LoadTimetable(station))
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

    @Test fun `handle SelectCategories event`() = coroutineRule.runBlockingTest {
        viewModel.offer(TimetableEvent.SelectCategories(setOf(Train.Category.Commuter)))
        verify(settingsRepository).setTrainCategories(setOf(Train.Category.Commuter))
    }

    @Test fun `handle SelectTimetableTypes event`() = coroutineRule.runBlockingTest {
        viewModel.offer(TimetableEvent.SelectTimetableTypes(setOf(TimetableRow.Type.Departure)))
        verify(settingsRepository).setTimetableTypes(setOf(TimetableRow.Type.Departure))
    }
}
