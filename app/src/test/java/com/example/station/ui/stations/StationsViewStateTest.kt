package com.example.station.ui.stations

import com.example.station.data.stations.LocalizedStationNames
import com.example.station.model.Station
import com.example.station.ui.stations.StationsResult as Result
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
        val result = state.reduce(Result.RecentStations(stations))
        assertThat(result.stations).isEmpty()
        assertThat(result.recentStations).isEqualTo(stations)
    }

    @Test fun `reduce state with NameMapper result`() {
        val stations = listOf(Station.of("first", 1), Station.of("second", 2))
        val state = StationsViewState.initial().copy(stations = stations, isLoadingNameMapper = true)
        val mapper = LocalizedStationNames.create(stations, mapOf(2 to "last"))
        val result = state.reduce(Result.NameMapper(mapper))
        assertThat(result.nameMapper).isEqualTo(mapper)
        assertThat(result.isLoading).isFalse()
        assertThat(result.stations).hasSize(2)
        assertThat(result.stations[0].name).isEqualTo("first")
        assertThat(result.stations[1].name).isEqualTo("last")
    }

    @Test fun `reduce state with NameMapper result when loading stations`() {
        val state = StationsViewState.initial().copy(
            isLoadingStations = true,
            isLoadingNameMapper = true
        )
        val mapper = LocalizedStationNames.create(emptyList())
        val result = state.reduce(Result.NameMapper(mapper))
        assertThat(result.nameMapper).isEqualTo(mapper)
        assertThat(result.isLoading).isTrue()
    }

    @Test fun `reduce state with LoadingStations result`() {
        val state = StationsViewState.initial()
        val result = state.reduce(Result.LoadingStations)
        assertThat(result.isLoading).isTrue()
    }

    @Test fun `reduce state with LoadingNameMapper result`() {
        val state = StationsViewState.initial()
        val result = state.reduce(Result.LoadingNameMapper)
        assertThat(result.isLoading).isTrue()
    }

    @Test fun `reduce state with StationsData result`() {
        val newStations = listOf(Station.of("A", 1), Station.of("B", 2))
        val state = StationsViewState.initial().copy(isLoadingStations = true)
        assertThat(state.isLoading).isTrue()
        val result = state.reduce(Result.StationsData(newStations))
        assertThat(result.isLoading).isFalse()
        assertThat(result.stations).isEqualTo(newStations)
    }

    @Test fun `reduce state with StationsData result when nameMapper is present`() {
        val newStations = listOf(Station.of("A", 1), Station.of("B", 2))
        val mapper = LocalizedStationNames.create(newStations, mapOf(1 to "C"))
        val state = StationsViewState.initial().copy(nameMapper = mapper)
        val result = state.reduce(Result.StationsData(newStations))
        assertThat(result.stations).hasSize(2)
        assertThat(result.stations[0].name).isEqualTo("B")
        assertThat(result.stations[0].uic).isEqualTo(2)
        assertThat(result.stations[1].name).isEqualTo("C")
        assertThat(result.stations[1].uic).isEqualTo(1)
    }

    @Test fun `reduce state with NoNewData result`() {
        val state = StationsViewState.initial().copy(isLoadingStations = true)
        val result = state.reduce(Result.NoNewData)
        assertThat(result.isLoading).isTrue()
    }

    @Test fun `reduce state with Error result`() {
        val state = StationsViewState.initial()
        val result = state.reduce(Result.Error("Oops!"))
        assertThat(result.errorMessage).isEqualTo("Oops!")
    }
}

/** Creates an instance of Station for testing purposes. */
fun Station.Companion.of(name: String, uic: Int): Station {
    return Station(true, Station.Type.Station, name, "", uic, "FI", 1.0, 1.0)
}
