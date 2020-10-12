package com.example.station.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test


class StationTest {

    @Test fun `secondary constructor creates a passenger station in Finland`() {
        val expected = Station(
            passengerTraffic = true, type = Station.Type.Station,
            name = "Station1", shortCode = "S1", uic = 1, countryCode = "FI",
            longitude = 10.0, latitude = 20.0
        )
        val result = Station("Station1", "S1", 1, 10.0, 20.0)
        assertThat(result).isEqualTo(expected)
    }
}
