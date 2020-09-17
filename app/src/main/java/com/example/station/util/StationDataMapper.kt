package com.example.station.util

import com.example.station.data.stations.cache.StationCacheEntity
import com.example.station.data.stations.network.StationNetworkEntity
import com.example.station.model.Station


/** Maps station network data transfer object into domain model. */
fun StationNetworkEntity.toDomainModel(): Station {
    return Station(
        passengerTraffic = passengerTraffic,
        type = Station.Type.of(type),
        name = name,
        shortCode = shortCode,
        uic = uic,
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
        uic = uic,
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
        uic = uic,
        countryCode = countryCode,
        longitude = longitude,
        latitude = latitude
    )
}
