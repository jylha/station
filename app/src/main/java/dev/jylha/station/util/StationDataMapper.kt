package dev.jylha.station.util

import dev.jylha.station.data.stations.cache.StationCacheEntity
import dev.jylha.station.data.stations.network.StationNetworkEntity
import dev.jylha.station.model.Station


/** Maps station network data transfer object into domain model. */
fun StationNetworkEntity.toDomainModel(): Station {
    return Station(
        passengerTraffic = passengerTraffic,
        type = Station.Type.of(type),
        name = name,
        shortCode = shortCode,
        code = code,
        countryCode = countryCode,
        longitude = longitude,
        latitude = latitude
    )
}

/** Maps station cache entity into domain model. */
fun StationCacheEntity.toDomainModel(): Station {
    return Station(
        passengerTraffic = passengerTraffic,
        type = Station.Type.of(type),
        name = name,
        shortCode = shortCode,
        code = uic,
        countryCode = countryCode,
        longitude = longitude,
        latitude = latitude
    )
}

/** Maps station domain object into cache entity. */
fun Station.toCacheEntity() : StationCacheEntity {
    return StationCacheEntity(
        passengerTraffic = passengerTraffic,
        type = type.value,
        name = name,
        shortCode = shortCode,
        uic = code,
        countryCode = countryCode,
        longitude = longitude,
        latitude = latitude
    )
}
