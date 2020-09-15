package com.example.station.util

import com.example.station.data.stations.network.StationNetworkEntity
import com.example.station.model.Station
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StationDataMapperTest {

    private val testEntity = StationNetworkEntity(
        passengerTraffic = true,
        type = "STATION",
        name = "Station Name",
        code = "SN",
        uicCode = 111,
        countryCode = "A",
        longitude = 50.0,
        latitude = 100.0
    )

    @Test
    fun `map station network DTO of type 'STATION' into domain model`() {
        val dto = testEntity
        val result = dto.toDomainModel()
        assertThat(result.type).isEqualTo(Station.Type.Station)
    }

    @Test
    fun `map station network DTO of type 'STOPPING_POINT' into domain model`() {
        val dto = testEntity.copy(type = "STOPPING_POINT")
        val result = dto.toDomainModel()
        assertThat(result.type).isEqualTo(Station.Type.StoppingPoint)
    }

    @Test
    fun `map station network DTO of type 'TURNOUT_IN_THE_OPEN_LINE' into domain model`() {
        val dto = testEntity.copy(type = "TURNOUT_IN_THE_OPEN_LINE")
        val result = dto.toDomainModel()
        assertThat(result.type).isEqualTo(Station.Type.TurnoutInTheOpenLine)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `map station network DTO of unknown type throws an exception`() {
        val dto  = testEntity.copy(type = "SOMETHING")
        dto.toDomainModel()
    }
}
