package com.example.station.ui.stations

import android.location.Location
import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.StoreResponse
import com.example.station.data.location.LocationService
import com.example.station.data.settings.SettingsRepository
import com.example.station.data.stations.StationNameMapper
import com.example.station.data.stations.StationRepository
import com.example.station.model.Station
import com.example.station.testutil.MainCoroutineScopeRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mockito.`when` as whenCalled

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class StationsViewModelTest {

    @get:Rule val coroutineRule: MainCoroutineScopeRule = MainCoroutineScopeRule()

    @Mock private lateinit var stationRepository: StationRepository
    @Mock private lateinit var settingsRepository: SettingsRepository
    @Mock private lateinit var locationService: LocationService

    private lateinit var viewModel: StationsViewModel

    private val testMapper = StationNameMapper { stationUic ->
        when (stationUic) {
            1 -> "Helsinki"
            else -> null
        }
    }
    private val testStations = listOf(
        Station("Helsinki", "HKI", 1, 50.0, 50.0)
    )

    @Before fun setup() = coroutineRule.runBlockingTest {
        whenCalled(stationRepository.getStationNameMapper()).thenReturn(testMapper)
        whenCalled(stationRepository.fetchStations()).thenReturn(
            flowOf(
                StoreResponse.Data(testStations, origin = ResponseOrigin.Cache)
            )
        )
        whenCalled(settingsRepository.recentStations()).thenReturn(flowOf(emptyList()))

        viewModel = StationsViewModel(stationRepository, settingsRepository, locationService)
    }

    @Test fun `initialize`() = coroutineRule.runBlockingTest {
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

    @Test fun `select nearest station`() = coroutineRule.runBlockingTest {
        val locationChannel = Channel<Location>()
        whenCalled(locationService.currentLocation()).thenReturn(locationChannel.consumeAsFlow())
        viewModel.setSelectionMode(selectNearestStation = true)

        with(viewModel.state.value) {
            assertThat(isFetchingLocation).isTrue()
            assertThat(latitude).isNull()
            assertThat(longitude).isNull()
        }

        val location = mock(Location::class.java)
        whenCalled(location.latitude).thenReturn(60.0)
        whenCalled(location.longitude).thenReturn(70.0)
        locationChannel.send(location)

        with(viewModel.state.value) {
            assertThat(isFetchingLocation).isFalse()
            assertThat(latitude).isEqualTo(60.0)
            assertThat(longitude).isEqualTo(70.0)
        }
    }

    @Test fun `set selected station`() = coroutineRule.runBlockingTest {
        val station = testStations.first()
        viewModel.stationSelected(station)
        verify(settingsRepository).setStation(station.uic)
    }
}
