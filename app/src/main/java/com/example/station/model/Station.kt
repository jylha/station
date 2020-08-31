package com.example.station.model

/**
 * Domain Model for station information.
 *
 * @param passengerTraffic Whether station supports commercial passenger traffic.
 * @param type Station type.
 * @param name Station name.
 * @param code Short station code.
 * @param uicCode Country specific UIC code [1-9999].
 * @param countryCode Country code.
 * @param longitude Longitude in WGS-84 format.
 * @param latitude Latitude in WGS-84 format.
 */
data class Station(
    val passengerTraffic: Boolean,
    val type: Type,
    val name: String,
    val code: String,
    val uicCode: Int,
    val countryCode: String,
    val longitude: Double,
    val latitude: Double
) {
    sealed class Type(val value: String) {
        object Station : Type("STATION")
        object StoppingPoint : Type("STOPPING_POINT")
        object TurnoutInTheOpenLine : Type("TURNOUT_IN_THE_OPEN_LINE")

        companion object {
            fun of(value: String): Type {
                return when (value) {
                    Station.value -> Station
                    StoppingPoint.value -> StoppingPoint
                    TurnoutInTheOpenLine.value -> TurnoutInTheOpenLine
                    else -> throw IllegalArgumentException("no matching station type")
                }
            }
        }
    }
}
