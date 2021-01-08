package dev.jylha.station.util

import dev.jylha.station.data.stations.cache.StationCacheEntity
import dev.jylha.station.data.stations.network.StationNetworkEntity
import dev.jylha.station.model.Station
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StationDataMapperTest {

    private val networkEntity = StationNetworkEntity(
        passengerTraffic = true,
        type = "STATION",
        name = "Station Name",
        shortCode = "SN",
        code = 111,
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

    @Test fun `station code is mapped correctly into domain model`() {
        val result = networkEntity.copy(code = 555).toDomainModel()
        assertThat(result.code).isEqualTo(555)
    }

    @Test fun `station country code is mapped correctly into domain model`() {
        val result = networkEntity.copy(countryCode = "FI").toDomainModel()
        assertThat(result.countryCode).isEqualTo("FI")
    }

    @Test fun `station longitude is mapped correctly into domain model`() {
        val result = networkEntity.copy(longitude = 56.7)
        assertThat(result.longitude).isEqualTo(56.7)
    }

    @Test fun `station latitude is mapped correctly into domain model`() {
        val result = networkEntity.copy(latitude = 123.4)
        assertThat(result.latitude).isEqualTo(123.4)
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

    @Test fun `map station cache DTO of type 'TURNOUT_IN_THE_OPEN_LINE' into domain model`() {
        val dto = cacheEntity.copy(type = "TURNOUT_IN_THE_OPEN_LINE")
        val result = dto.toDomainModel()
        assertThat(result.type).isEqualTo(Station.Type.TurnoutInTheOpenLine)
    }

    @Test fun `station name and code are correctly mapped from cache DTO into domain model`() {
        val dto = cacheEntity.copy(name = "Name", uic = 100)
        val result = dto.toDomainModel()
        assertThat(result.name).isEqualTo("Name")
        assertThat(result.code).isEqualTo(100)
    }

    @Test fun `station short code is correctly mapped from cache DTO into domain model`() {
        val dto = cacheEntity.copy(shortCode = "ABC")
        val result = dto.toDomainModel()
        assertThat(result.shortCode).isEqualTo("ABC")
    }

    @Test fun `station latitude is mapped correctly from cache DTO into domain model`() {
        val dto = cacheEntity.copy(latitude = 111.1)
        val result = dto.toDomainModel()
        assertThat(result.latitude).isEqualTo(111.1)
    }

    @Test fun `station longitude is mapped correctly from cache DTO into domain model`() {
        val dto = cacheEntity.copy(longitude = 67.8)
        val result = dto.toDomainModel()
        assertThat(result.longitude).isEqualTo(67.8)
    }

    private val domainEntity = Station(
        passengerTraffic = true,
        type = Station.Type.Station,
        name = "Station Name",
        shortCode = "SN",
        code = 333,
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

    @Test fun `map station of type TurnoutInTheOpenLine into cache entity`() {
        val station = domainEntity.copy(type = Station.Type.TurnoutInTheOpenLine)
        val result = station.toCacheEntity()
        assertThat(result.type).isEqualTo("TURNOUT_IN_THE_OPEN_LINE")
    }

    @Test fun `station name and code are correctly mapped from domain model into cache entity`() {
        val station = domainEntity.copy(name = "Name", code = 200)
        val result = station.toCacheEntity()
        assertThat(result.name).isEqualTo("Name")
        assertThat(result.uic).isEqualTo(200)
    }

    @Test fun `station short code is correctly mapped into cache entity`() {
        val station = domainEntity.copy(shortCode = "code")
        val result = station.toCacheEntity()
        assertThat(result.shortCode).isEqualTo("code")
    }

    @Test fun `station county code is mapped correctly into cache entity`() {
        val station = domainEntity.copy(countryCode = "AB")
        val result = station.toCacheEntity()
        assertThat(result.countryCode).isEqualTo("AB")
    }

    @Test fun `station latitude is mapped correctly into cache entity`() {
        val station = domainEntity.copy(latitude = 123.4)
        val result = station.toCacheEntity()
        assertThat(result.latitude).isEqualTo(123.4)
    }

    @Test fun `station longitude is mapped correctly into cache entity`() {
        val station = domainEntity.copy(longitude = 123.4)
        val result = station.toCacheEntity()
        assertThat(result.longitude).isEqualTo(123.4)
    }
}
