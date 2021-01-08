package dev.jylha.station.ui.stations

import dev.jylha.station.data.stations.LocalizedStationNames
import dev.jylha.station.model.Station
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

    @Test fun `reduce state with RecentStationsUpdated result`() {
        val state = StationsViewState.initial()
        val stations = listOf(1, 2, 3)
        val result = state.reduce(RecentStationsUpdated(stations))
        assertThat(result.stations).isEmpty()
        assertThat(result.recentStations).isEqualTo(stations)
    }

    @Test fun `reduce state with LoadNameMapper_Loading result`() {
        val state = StationsViewState.initial()
        val result = state.reduce(LoadNameMapper.Loading)
        assertThat(result.isLoading).isTrue()
    }

    @Test fun `reduce state with LoadNameMapper_Success result`() {
        val stations = listOf(Station.of("first", 1), Station.of("second", 2))
        val state =
            StationsViewState.initial().copy(stations = stations, isLoadingNameMapper = true)
        val mapper = LocalizedStationNames.from(stations, mapOf(2 to "last"))
        val result = state.reduce(LoadNameMapper.Success(mapper))
        assertThat(result.nameMapper).isEqualTo(mapper)
        assertThat(result.isLoading).isFalse()
        assertThat(result.stations).hasSize(2)
        assertThat(result.stations[0].name).isEqualTo("first")
        assertThat(result.stations[1].name).isEqualTo("last")
    }

    @Test fun `reduce state with LoadNameMapper_Success result when loading stations`() {
        val state = StationsViewState(isLoadingStations = true, isLoadingNameMapper = true)
        val mapper = LocalizedStationNames.from(emptyList())
        val result = state.reduce(LoadNameMapper.Success(mapper))
        assertThat(result.nameMapper).isEqualTo(mapper)
        assertThat(result.isLoading).isTrue()
    }

    @Test fun `reduce state with LoadNameMapper_Error result`() {
        val state = StationsViewState(isLoadingNameMapper = true)
        val message = "Fail"
        val result = state.reduce(LoadNameMapper.Error(message))
        assertThat(result.isLoading).isFalse()
        assertThat(result.errorMessage).isEqualTo(message)
    }

    @Test fun `reduce state with LoadStations_Loading result`() {
        val state = StationsViewState.initial()
        val result = state.reduce(LoadStations.Loading)
        assertThat(result.isLoading).isTrue()
    }

    @Test fun `reduce state with LoadStations_Reloading result`() {
        val state = StationsViewState.initial()
        val result = state.reduce(LoadStations.Reloading)
        assertThat(result.isLoading).isFalse()
        assertThat(result.isReloadingStations).isTrue()
    }

    @Test fun `reduce state with LoadStations_NoNewData result while reloading`() {
        val state = StationsViewState.initial().copy(isReloadingStations = true)
        val result = state.reduce(LoadStations.NoNewData)
        assertThat(result.isReloadingStations).isFalse()
    }

    @Test fun `reduce state with LoadStations_Success result while reloading`() {
        val newStations = listOf(
            Station.of("C", 3), Station.of("D", 4)
        )
        val state = StationsViewState(
            isReloadingStations = true, stations = listOf(
                Station.of("A", 1), Station.of("B", 2)
            )
        )
        val result = state.reduce(LoadStations.Success(newStations))
        assertThat(result.isLoading).isFalse()
        assertThat(result.isReloadingStations).isFalse()
        assertThat(result.stations).isEqualTo(newStations)
    }

    @Test fun `reduce state with LoadStations_Success result`() {
        val newStations = listOf(Station.of("A", 1), Station.of("B", 2))
        val state = StationsViewState.initial().copy(isLoadingStations = true)
        assertThat(state.isLoading).isTrue()
        val result = state.reduce(LoadStations.Success(newStations))
        assertThat(result.isLoading).isFalse()
        assertThat(result.stations).isEqualTo(newStations)
    }

    @Test fun `reduce state with LoadStations_Success result when nameMapper is present`() {
        val newStations = listOf(Station.of("A", 1), Station.of("B", 2))
        val mapper = LocalizedStationNames.from(newStations, mapOf(1 to "C"))
        val state = StationsViewState.initial().copy(nameMapper = mapper)
        val result = state.reduce(LoadStations.Success(newStations))
        assertThat(result.stations).hasSize(2)
        assertThat(result.stations[0].name).isEqualTo("B")
        assertThat(result.stations[0].code).isEqualTo(2)
        assertThat(result.stations[1].name).isEqualTo("C")
        assertThat(result.stations[1].code).isEqualTo(1)
    }

    @Test fun `reduce state with LoadStations_NoNewData result`() {
        val state = StationsViewState.initial().copy(isLoadingStations = true)
        val result = state.reduce(LoadStations.NoNewData)
        assertThat(result.isLoading).isFalse()
    }

    @Test fun `reduce state with LoadStations_Error result`() {
        val state = StationsViewState.initial()
        val result = state.reduce(LoadStations.Error("Oops!"))
        assertThat(result.isLoading).isFalse()
        assertThat(result.errorMessage).isEqualTo("Oops!")
    }

    @Test fun `reduce state with FetchLocation_Fetching result`() {
        val state = StationsViewState(
            isFetchingLocation = false,
            nearestStation = Station("A", "a", 1, 1.0, 2.0)
        )
        val result = state.reduce(FetchLocation.Fetching)
        assertThat(result.isFetchingLocation).isTrue()
        assertThat(result.selectNearest).isTrue()
        assertThat(result.nearestStation).isNull()
    }

    @Test fun `reduce state with FetchLocation_Success result when no stations`() {
        val state = StationsViewState(isFetchingLocation = true, selectNearest = true)
        val latitude = 1.0
        val longitude = 2.0
        val result = state.reduce(FetchLocation.Success(latitude, longitude))
        assertThat(result.latitude).isEqualTo(latitude)
        assertThat(result.longitude).isEqualTo(longitude)
        assertThat(result.nearestStation).isNull()
    }

    @Test fun `reduce state with FetchLocation_Error result`() {
        val state = StationsViewState(isFetchingLocation = true, selectNearest = true)
        val message = "Error."
        val result = state.reduce(FetchLocation.Error(message))
        assertThat(result.isFetchingLocation).isFalse()
        assertThat(result.selectNearest).isFalse()
        assertThat(result.errorMessage).isEqualTo(message)
    }

    @Test fun `reduce state with FetchLocation_Cancel result`() {
        val station = Station("A", "a", 1, 10.0, 10.0)
        val state = StationsViewState(selectNearest = true, nearestStation = station)
        val result = state.reduce(FetchLocation.Cancel)
        assertThat(result.nearestStation).isNull()
        assertThat(result.selectNearest).isFalse()
    }
}

/** Creates an instance of Station for testing purposes. */
fun Station.Companion.of(name: String, code: Int): Station {
    return Station(true, Station.Type.Station, name, "", code, "FI", 1.0, 1.0)
}
