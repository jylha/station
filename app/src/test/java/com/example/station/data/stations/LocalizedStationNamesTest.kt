package com.example.station.data.stations

import com.example.station.model.Station
import com.google.common.truth.Truth.assertThat
import org.junit.Test


class LocalizedStationNamesTest {

    private fun station(name: String, shortCode: String, code: Int) = Station(
        true, Station.Type.Station, name, shortCode, code,
        "FI", 0.0, 0.0
    )

    @Test fun `create LocalizedStationNames from empty station list`() {
        val result = LocalizedStationNames.from(emptyList())
        assertThat(result).isInstanceOf(LocalizedStationNames::class.java)
        assertThat(result.map).isEmpty()
    }

    @Test fun `create LocalizedStationNames from list of stations`() {
        val stations = listOf(
            station("Station1", "s1", 1),
            station("Station2", "s2", 2),
            station("Station3", "s3", 3),
        )
        val result = LocalizedStationNames.from(stations)
        assertThat(result.map).containsExactly(
            1, "Station1", 2, "Station2", 3, "Station3"
        )
    }

    @Test fun `create LocalizedStationName from list of stations and localized names`() {
        val stations = listOf(
            station("Station1", "s1", 1),
            station("Station2", "s1", 2),
            station("Station3", "s1", 3)
        )
        val localizedNames = mapOf(1 to "Localized1", 3 to "Localized3")
        val result = LocalizedStationNames.from(stations, localizedNames)
        assertThat(result.map).containsExactly(
            1, "Localized1", 2, "Station2", 3, "Localized3"
        )
    }

    @Test fun `stationName() returns correct name for given station code`() {
        val stations = listOf(
            station("Station1", "s1", 1),
            station("Station2", "s1", 2),
        )
        val localizedNames = mapOf(1 to "Localized1")
        val mapper = LocalizedStationNames.from(stations, localizedNames)
        val result = mapper.stationName(1)
        assertThat(result).isEqualTo("Localized1")
    }

    @Test fun `stationName() returns null when station code is not found`() {
        val stations = listOf(
            station("Station2", "s1", 2),
        )
        val localizedNames = mapOf(1 to "Localized1")
        val mapper = LocalizedStationNames.from(stations, localizedNames)
        val result = mapper.stationName(1)
        assertThat(result).isNull()
    }
}
