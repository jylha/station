package dev.jylha.station.ui.train

import dev.jylha.station.data.stations.StationNameMapper
import dev.jylha.station.data.stations.StationRepository
import dev.jylha.station.data.trains.TrainRepository
import dev.jylha.station.model.Train
import dev.jylha.station.testutil.MainCoroutineScopeRule
import com.google.common.truth.Truth.assertThat
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class TrainDetailsViewModelTest {

    @get:Rule val coroutineScopeRule: MainCoroutineScopeRule = MainCoroutineScopeRule()

    @Mock private lateinit var trainRepository: TrainRepository
    @Mock private lateinit var stationRepository: StationRepository

    private lateinit var viewModel: TrainDetailsViewModel
    private val testMapper = StationNameMapper { stationCode ->
        when (stationCode) {
            1 -> "Helsinki"
            else -> null
        }
    }

    @Before fun setup() = runBlockingTest {
        whenCalled(stationRepository.getStationNameMapper()).thenReturn(testMapper)
        viewModel = TrainDetailsViewModel(trainRepository, stationRepository)
    }

    @Test fun `initialize view model`() {
        val result = viewModel.state.value
        val excepted = TrainDetailsViewState(
            isLoadingTrain = false,
            isLoadingMapper = false,
            isReloading = false,
            train = null,
            nameMapper = testMapper
        )
        assertThat(result).isEqualTo(excepted)
    }

    @Test fun `set train`() = runBlockingTest {
        val train = Train(1, "A", Train.Category.LongDistance)
        val departureDate = train.departureDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        whenCalled(trainRepository.train(departureDate, train.number)).thenReturn(train)
        viewModel.setTrain(departureDate, train.number)
        val result = viewModel.state.value
        val excepted = TrainDetailsViewState(
            isLoadingTrain = false,
            isLoadingMapper = false,
            isReloading = false,
            train = train,
            nameMapper = testMapper
        )
        assertThat(result).isEqualTo(excepted)
    }

    @Test fun `reload train details`() = runBlockingTest {
        val train = Train(1, "A", Train.Category.LongDistance, isRunning = false, version = 100)
        val updated = Train(1, "A", Train.Category.LongDistance, isRunning = true, version = 200)
        val departureDate = train.departureDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        whenCalled(trainRepository.train(departureDate, 1, 100)).thenReturn(updated)
        viewModel.reload(train)
        val result = viewModel.state.value
        val expected = TrainDetailsViewState(
            isLoadingTrain = false,
            isLoadingMapper = false,
            isReloading = false,
            train = updated,
            nameMapper = testMapper
        )
        assertThat(result).isEqualTo(expected)
    }
}
