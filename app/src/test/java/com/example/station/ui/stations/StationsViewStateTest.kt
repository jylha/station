package com.example.station.ui.stations

import com.google.common.truth.Truth.assertThat
import org.junit.Test


class StationsViewStateTest {

    @Test fun `initial state`() {
        val result = StationsViewState.initial()
        assertThat(result.stations).isEmpty()
        assertThat(result.recentStations).isEmpty()
        assertThat(result.isLoading).isFalse()
    }

    @Test fun `reduce state with RecentStations result`() {
        val recent = listOf(1, 2, 3)
        val state = StationsViewState.initial()
        val result = state.reduce(StationViewResult.RecentStations(recent))
        assertThat(result.stations).isEmpty()
        assertThat(result.recentStations).isEqualTo(recent)
    }
}
