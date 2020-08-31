package com.example.station.util

import com.example.station.data.stations.network.StationNetworkEntity
import com.example.station.model.Station


/** Maps station network data transfer object into domain model. */
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

private fun String.asStationType() = Station.Type.of(this)


