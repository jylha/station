package com.example.station.data.stations

import com.example.station.model.Station
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StationNameMapperTest {

    @Test fun `rename empty list returns empty list`() {
        val stations = emptyList<Station>()
        val mapper = StationNameMapper { null }
        val result = mapper.rename(stations)
        assertThat(result).isEmpty()
    }

    @Test fun `rename stations with empty mapper`() {
        val stations = listOf(
            Station("Station1", "s1", 1, 1.0, 2.0),
            Station("Station2", "s2", 2, 1.0, 2.0)
        )
        val mapper = StationNameMapper { null }
        val result = mapper.rename(stations)
        assertThat(result).isEqualTo(stations)
    }

    @Test fun `rename stations with mapper`() {
        val stations = listOf(
            Station("Station1", "s1", 1, 1.0, 2.0),
            Station("Station2", "s2", 2, 1.0, 2.0)
        )
        val mapper = StationNameMapper { stationCode ->
            when (stationCode) {
                1 -> "Renamed station 1"
                2 -> "Renamed station 2"
                else -> null
            }
        }
        val expected = listOf(
            Station("Renamed station 1", "s1", 1, 1.0, 2.0),
            Station("Renamed station 2", "s2", 2, 1.0, 2.0)
        )
        val result = mapper.rename(stations)
        assertThat(result).isEqualTo(expected)
    }

    @Test fun `rename and sort stations with mapper`() {
        val stations = listOf(
            Station("A", "a", 1, 1.0, 2.0),
            Station("B", "b", 2, 1.0, 2.0),
            Station("C", "c", 3, 1.0, 2.0)
        )
        val mapper = StationNameMapper { stationCode ->
            when (stationCode) {
                1 -> "D"
                2 -> "E"
                else -> null
            }
        }
        val expected = listOf(
            Station("C", "c", 3, 1.0, 2.0),
            Station("D", "a", 1, 1.0, 2.0),
            Station("E", "b", 2, 1.0, 2.0)
        )
        val result = mapper.renameAndSort(stations)
        assertThat(result).isEqualTo(expected)
    }
}
