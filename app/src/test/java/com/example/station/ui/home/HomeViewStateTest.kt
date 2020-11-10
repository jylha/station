package com.example.station.ui.home

import com.example.station.model.Station
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class HomeViewStateTest {

    @Test fun `initial state`() {
        val result = HomeViewState.initial()
        assertThat(result.isLoadingSettings).isFalse()
        assertThat(result.isLoadingStation).isFalse()
        assertThat(result.isLoading).isFalse()
    }

    @Test fun `reduce state with LoadSettings_Loading result`() {
        val state = HomeViewState(isLoadingSettings = false)
        val result = state.reduce(LoadSettings.Loading)
        assertThat(result.isLoadingSettings).isTrue()
        assertThat(result.isLoading).isTrue()
    }

    @Test fun `reduce state with LoadSettings_Success result`() {
        val state = HomeViewState(isLoadingSettings = true)
        val result = state.reduce(LoadSettings.Success(null))
        assertThat(result.isLoadingSettings).isFalse()
        assertThat(result.isLoadingStation).isFalse()
        assertThat(result.isLoading).isFalse()
        assertThat(result.stationCode).isEqualTo(null)
    }

    @Test fun `reduce state with LoadStation_Loading result`() {
        val state = HomeViewState(isLoadingSettings = true)
        val result = state.reduce(LoadStation.Loading)
        assertThat(result.isLoadingSettings).isFalse()
        assertThat(result.isLoadingStation).isTrue()
        assertThat(result.isLoading).isTrue()
    }

    @Test fun `reduce state with LoadStation_Success result`() {
        val state = HomeViewState(isLoadingStation = true)
        val station = Station("A", "a", 1, 1.0, 10.0)
        val result = state.reduce(LoadStation.Success(station))
        assertThat(result.isLoadingStation).isFalse()
        assertThat(result.station).isEqualTo(station)
    }

    @Test fun `reduce state with LoadStation_Error result`() {
        val state = HomeViewState(isLoadingStation = true)
        val result = state.reduce(LoadStation.Error("Error"))
        assertThat(result.isLoadingStation).isFalse()
        assertThat(result.isLoading).isFalse()
    }
}
