package dev.jylha.station.ui.stations

import android.location.Location
import com.google.common.truth.Truth.assertThat
import dev.jylha.station.data.settings.SettingsRepository
import dev.jylha.station.data.stations.StationNameMapper
import dev.jylha.station.data.stations.StationRepository
import dev.jylha.station.domain.GetLocationUseCase
import dev.jylha.station.model.Station
import dev.jylha.station.testutil.CoroutineScopeRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mobilenativefoundation.store.store5.StoreReadResponse
import org.mobilenativefoundation.store.store5.StoreReadResponseOrigin
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mockito.`when` as whenCalled

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class StationsViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    @get:Rule val coroutineRule = CoroutineScopeRule(dispatcher)

    @Mock private lateinit var stationRepository: StationRepository
    @Mock private lateinit var settingsRepository: SettingsRepository

    private val fakeLocation = object : GetLocationUseCase {
        override suspend fun invoke(): Location {
            delay(100)
            val location = mock(Location::class.java)
            whenCalled(location.latitude).thenReturn(60.0)
            whenCalled(location.longitude).thenReturn(70.0)
            return location
        }
    }

    private lateinit var viewModel: StationsViewModel

    private val testMapper = StationNameMapper { stationCode ->
        when (stationCode) {
            1 -> "Helsinki"
            else -> null
        }
    }
    private val testStations = listOf(
        Station("Helsinki", "HKI", 1, 50.0, 50.0)
    )

    @Before fun setup() = runTest(dispatcher) {
        whenCalled(stationRepository.getStationNameMapper()).thenReturn(testMapper)
        whenCalled(stationRepository.fetchStations()).thenReturn(
            flowOf(
                StoreReadResponse.Data(testStations, origin = StoreReadResponseOrigin.Cache)
            )
        )
        whenCalled(settingsRepository.recentStations()).thenReturn(flowOf(emptyList()))

        viewModel = StationsViewModel(stationRepository, settingsRepository, fakeLocation)
    }

    @Test fun `initialize view model`() = runTest(dispatcher) {
        val expected = StationsViewState(
            stations = testStations,
            recentStations = emptyList(),
            nameMapper = testMapper,
            isLoadingStations = false,
            isLoadingNameMapper = false,
            isReloadingStations = false,
            selectNearest = false,
            isFetchingLocation = false,
            longitude = null,
            latitude = null,
            nearestStation = null,
            errorMessage = null
        )
        val result = viewModel.state.value
        assertThat(result).isEqualTo(expected)
    }

    @Test fun `select nearest station`() = runTest(dispatcher) {
        viewModel.setSelectionMode(selectNearestStation = true)
        advanceTimeBy(10)

        with(viewModel.state.value) {
            assertThat(selectNearest).isTrue()
            assertThat(isFetchingLocation).isTrue()
            assertThat(latitude).isNull()
            assertThat(longitude).isNull()
        }

        advanceTimeBy(100)

        with(viewModel.state.value) {
            assertThat(isFetchingLocation).isFalse()
            assertThat(latitude).isEqualTo(60.0)
            assertThat(longitude).isEqualTo(70.0)
        }
    }

    @Test fun `set selected station`() = runTest(dispatcher) {
        val station = testStations.first()
        viewModel.stationSelected(station)
        advanceUntilIdle()
        verify(settingsRepository).setStation(station.code)
    }

    @Test fun `change mode back to selecting station from list`() = runTest(dispatcher) {
        viewModel.setSelectionMode(selectNearestStation = true)
        advanceTimeBy(50)

        with(viewModel.state.value) {
            assertThat(selectNearest).isTrue()
            assertThat(isFetchingLocation).isTrue()
            assertThat(latitude).isNull()
            assertThat(longitude).isNull()
        }

        viewModel.setSelectionMode(selectNearestStation = false)
        advanceTimeBy(10)

        with(viewModel.state.value) {
            assertThat(selectNearest).isFalse()
            assertThat(isFetchingLocation).isFalse()
        }
    }
}
