package com.example.station.util

import com.example.station.data.stations.network.StationNetworkEntity
import com.example.station.model.Station

fun Station.toNetworkEntity(): StationNetworkEntity {
    return StationNetworkEntity(
        passengerTraffic = this.passengerTraffic,
        type = this.type.asString(),
        name = this.name,
        code = this.code,
        uicCode = this.uicCode,
        countryCode = this.countryCode,
        longitude = this.longitude,
        latitude = this.latitude
    )
}

fun StationNetworkEntity.toDomainObject(): Station {
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

private fun Station.Type.asString() = this.value
private fun String.asStationType() = Station.Type.of(this)


