package com.example.station.model


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
            fun of(value: String) : Type {
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
