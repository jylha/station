package com.example.station.util

import com.example.station.data.stations.cache.StationCacheEntity
import com.example.station.data.stations.network.StationNetworkEntity
import com.example.station.model.Station
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StationDataMapperTest {

    private val networkEntity = StationNetworkEntity(
        passengerTraffic = true,
        type = "STATION",
        name = "Station Name",
        shortCode = "SN",
        uic = 111,
        countryCode = "A",
        longitude = 50.0,
        latitude = 100.0
    )

    @Test fun `map station network DTO of type 'STATION' into domain model`() {
        val dto = networkEntity
        val result = dto.toDomainModel()
        assertThat(result.type).isEqualTo(Station.Type.Station)
    }

    @Test fun `map station network DTO of type 'STOPPING_POINT' into domain model`() {
        val dto = networkEntity.copy(type = "STOPPING_POINT")
        val result = dto.toDomainModel()
        assertThat(result.type).isEqualTo(Station.Type.StoppingPoint)
    }

    @Test fun `map station network DTO of type 'TURNOUT_IN_THE_OPEN_LINE' into domain model`() {
        val dto = networkEntity.copy(type = "TURNOUT_IN_THE_OPEN_LINE")
        val result = dto.toDomainModel()
        assertThat(result.type).isEqualTo(Station.Type.TurnoutInTheOpenLine)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `map station network DTO of unknown type throws an exception`() {
        val dto = networkEntity.copy(type = "SOMETHING")
        dto.toDomainModel()
    }

    @Test fun `station name is mapped correctly into domain model`() {
        val result = networkEntity.copy(name = "Nowhere").toDomainModel()
        assertThat(result.name).isEqualTo("Nowhere")
    }

    @Test fun `station short code is mapped correctly into domain model`() {
        val result = networkEntity.copy(shortCode = "ABC").toDomainModel()
        assertThat(result.shortCode).isEqualTo("ABC")
    }

    @Test fun `station UIC is mapped correctly into domain model`() {
        val result = networkEntity.copy(uic = 555).toDomainModel()
        assertThat(result.uic).isEqualTo(555)
    }

    private val cacheEntity = StationCacheEntity(
        passengerTraffic = true,
        type = "STATION",
        name = "Station Name",
        shortCode = "SN",
        uic = 222,
        countryCode = "B",
        longitude = 50.0,
        latitude = 100.0
    )

    @Test fun `map station cache DTO of type 'STATION' into domain model`() {
        val dto = cacheEntity.copy(type = "STATION")
        val result = dto.toDomainModel()
        assertThat(result.type).isEqualTo(Station.Type.Station)
    }

    @Test fun `map station cache DTO of type 'STOPPING_POINT' into domain model`() {
        val dto = cacheEntity.copy(type = "STOPPING_POINT")
        val result = dto.toDomainModel()
        assertThat(result.type).isEqualTo(Station.Type.StoppingPoint)
    }

    @Test fun `station name and uic are correctly mapped from cache DTO into domain model`() {
        val dto = cacheEntity.copy(name = "Name", uic = 100)
        val result = dto.toDomainModel()
        assertThat(result.name).isEqualTo("Name")
        assertThat(result.uic).isEqualTo(100)
    }

    private val domainEntity = Station(
        passengerTraffic = true,
        type = Station.Type.Station,
        name = "Station Name",
        shortCode = "SN",
        uic = 333,
        countryCode = "C",
        longitude = 50.0,
        latitude = 50.0
    )

    @Test fun `map station of type Station into cache entity`() {
        val station = domainEntity.copy(type = Station.Type.Station)
        val result = station.toCacheEntity()
        assertThat(result.type).isEqualTo("STATION")
    }

    @Test fun `map station of type StoppingPoint into cache entity`() {
        val station = domainEntity.copy(type = Station.Type.StoppingPoint)
        val result = station.toCacheEntity()
        assertThat(result.type).isEqualTo("STOPPING_POINT")
    }

    @Test fun `station name and uic are correctly mapped from domain model into cache entity`() {
        val station = domainEntity.copy(name = "Name", uic = 200)
        val result = station.toCacheEntity()
        assertThat(result.name).isEqualTo("Name")
        assertThat(result.uic).isEqualTo(200)
    }
}
