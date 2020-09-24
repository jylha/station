package com.example.station.ui.stations

import com.example.station.data.stations.LocalizedStationNames
import com.example.station.model.Station
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StationsViewStateTest {

    @Test fun `initial state`() {
        val result = StationsViewState.initial()
        assertThat(result.stations).isEmpty()
        assertThat(result.recentStations).isEmpty()
        assertThat(result.isLoading).isFalse()
        assertThat(result.nameMapper).isNull()
    }

    @Test fun `reduce state with RecentStations result`() {
        val state = StationsViewState.initial()
        val stations = listOf(1, 2, 3)
        val result = state.reduce(StationViewResult.RecentStations(stations))
        assertThat(result.stations).isEmpty()
        assertThat(result.recentStations).isEqualTo(stations)
    }

    @Test fun `reduce state with NameMapper result`() {
        val stations = listOf(Station.of("first", 1), Station.of("second", 2))
        val state = StationsViewState.initial().copy(stations = stations)
        val mapper = LocalizedStationNames.create(stations, mapOf(2 to "last"))
        val result = state.reduce(StationViewResult.NameMapper(mapper))
        assertThat(result.nameMapper).isEqualTo(mapper)
        assertThat(result.stations).hasSize(2)
        assertThat(result.stations[0].name).isEqualTo("first")
        assertThat(result.stations[1].name).isEqualTo("last")
    }
}

/** Creates an instance of Station for testing purposes. */
fun Station.Companion.of(name: String, uic: Int): Station {
    return Station(true, Station.Type.Station, name, "", uic, "FI", 1.0, 1.0)
}
