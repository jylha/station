package com.example.station.ui.timetable

import com.example.station.data.settings.SettingsRepository
import com.example.station.data.stations.StationNameMapper
import com.example.station.data.stations.StationRepository
import com.example.station.data.trains.TrainRepository
import com.example.station.model.CauseCategories
import com.example.station.model.TimetableRow
import com.example.station.model.Train
import com.example.station.testutil.MainCoroutineScopeRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
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

    private val testMapper = object : StationNameMapper {
        override fun stationName(stationUic: Int): String? = when (stationUic) {
            1 -> "Helsinki"
            else -> null
        }
    }

    private val timetable = listOf(
        Train(1, "IC", Train.Category.LongDistance),
        Train(2, "S", Train.Category.LongDistance)
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
}
