package com.example.station.util

import com.example.station.data.stations.cache.StationCacheEntity
import com.example.station.data.stations.network.StationNetworkEntity
import com.example.station.model.Station


/** Maps station network data transfer object into domain model. */
fun StationNetworkEntity.toDomainModel(): Station {
    return Station(
        passengerTraffic = this.passengerTraffic,
        type = this.type.asStationType(),
        name = this.name,
        code = this.code,
        uicCode = this.uicCode,
        countryCode = this.countryCode,
        longitude = this.longitude,
        latitude = this.latitude
    )
}

private fun String.asStationType() = Station.Type.of(this)

/** Maps station cache entity into domain model. */
fun StationCacheEntity.toDomainModel(): Station {
    return Station(
        passengerTraffic = this.passengerTraffic,
        type = Station.Type.Station,
        name = this.name,
        code = this.code,
        uicCode = this.uicCode,
        countryCode = this.countryCode,
        longitude = this.longitude,
        latitude = this.latitude
    )
}

/** Maps station domain object into cache entity. */
fun Station.toCacheEntity() : StationCacheEntity {
    require(this.type == Station.Type.Station)
    return StationCacheEntity(
        passengerTraffic = this.passengerTraffic,
        name = this.name,
        code = this.code,
        uicCode = this.uicCode,
        countryCode = this.countryCode,
        longitude = this.longitude,
        latitude = this.latitude
    )
}
