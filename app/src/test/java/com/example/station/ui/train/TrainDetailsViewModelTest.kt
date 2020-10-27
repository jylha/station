package com.example.station.ui.train

import com.example.station.data.stations.StationNameMapper
import com.example.station.data.stations.StationRepository
import com.example.station.data.trains.TrainRepository
import com.example.station.model.Train
import com.example.station.utils.MainCoroutineScopeRule
import com.google.common.truth.Truth.assertThat
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
    private val testMapper = object : StationNameMapper {
        override fun stationName(stationUic: Int): String? {
            return when (stationUic) {
                1 -> "Helsinki"
                else -> null
            }
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
        viewModel.setTrain(train)
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
        whenCalled(trainRepository.train(1, 100)).thenReturn(updated)
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
