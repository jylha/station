package dev.jylha.station.ui.home

import dev.jylha.station.data.settings.SettingsRepository
import dev.jylha.station.data.stations.StationRepository
import dev.jylha.station.testutil.MainCoroutineScopeRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @get:Rule val coroutineScopeRule: MainCoroutineScopeRule = MainCoroutineScopeRule()

    @Mock private lateinit var settingsRepository: SettingsRepository
    @Mock private lateinit var stationRepository: StationRepository

    private lateinit var viewModel: HomeViewModel

    @Test fun `initialize view model`() = runBlockingTest {
        //whenCalled(settingsRepository.station()).thenReturn(flowOf(null))
        viewModel = HomeViewModel(settingsRepository, stationRepository)
        val result = viewModel.state.value
        val expected = HomeViewState(
            isLoadingSettings = false,
            isLoadingStation = false,
            stationCode = null,
            station = null
        )
        assertThat(result).isEqualTo(expected)
    }
}
