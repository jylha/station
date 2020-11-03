package com.example.station.data.stations

/** A functional interface for mapping station UIC to corresponding station name. */
fun interface StationNameMapper {

    /**
     * Returns the name of the station with given [stationUic].
     * @param stationUic UIC of the station.
     * @return Station name or null if matching station is not found.
     */
    fun stationName(stationUic: Int): String?
}
