package dev.jylha.station.ui.home

import dev.jylha.station.data.settings.SettingsRepository
import dev.jylha.station.data.stations.StationRepository
import com.google.common.truth.Truth.assertThat
import dev.jylha.station.testutil.CoroutineScopeRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    @get:Rule val coroutineRule = CoroutineScopeRule(dispatcher)

    @Mock private lateinit var settingsRepository: SettingsRepository
    @Mock private lateinit var stationRepository: StationRepository

    private lateinit var viewModel: HomeViewModel

    @Test fun `initialize view model`() = runTest(dispatcher) {
        viewModel = HomeViewModel(settingsRepository, stationRepository)
        val result = viewModel.state.value
        val expected = HomeViewState(
            isLoadingSettings = false,
            isLoadingStation = false,
            station = null
        )
        assertThat(result).isEqualTo(expected)
    }
}
