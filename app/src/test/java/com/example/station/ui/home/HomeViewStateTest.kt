package com.example.station.ui.home

import com.example.station.model.Station
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class HomeViewStateTest {

    @Test fun `initial state`() {
        val result = HomeViewState.initial()
        assertThat(result.isLoadingSettings).isTrue()
        assertThat(result.isLoadingStation).isFalse()
        assertThat(result.isLoading).isTrue()
    }

    @Test fun `reduce state with LoadingSettings result`() {
        val state = HomeViewState(isLoadingSettings = false)
        val result = state.reduce(HomeViewResult.LoadingSettings)
        assertThat(result.isLoadingSettings).isTrue()
        assertThat(result.isLoading).isTrue()
    }

    @Test fun `reduce state with SettingsLoaded result`() {
        val state = HomeViewState(isLoadingSettings = false)
        val result = state.reduce(HomeViewResult.SettingsLoaded)
        assertThat(result.isLoadingSettings).isFalse()
        assertThat(result.isLoadingStation).isFalse()
        assertThat(result.isLoading).isFalse()
    }

    @Test fun `reduce state with LoadingStation result`() {
        val state = HomeViewState(isLoadingSettings = true)
        val result = state.reduce(HomeViewResult.LoadingStation)
        assertThat(result.isLoadingSettings).isFalse()
        assertThat(result.isLoadingStation).isTrue()
        assertThat(result.isLoading).isTrue()
    }

    @Test fun `reduce state with StationLoaded result`() {
        val state = HomeViewState(isLoadingStation = true)
        val station = Station("A", "a", 1, 1.0, 10.0)
        val result = state.reduce(HomeViewResult.StationLoaded(station))
        assertThat(result.isLoadingStation).isFalse()
        assertThat(result.station).isEqualTo(station)
    }
}
