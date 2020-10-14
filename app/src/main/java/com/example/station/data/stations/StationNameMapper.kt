package com.example.station.data.stations

/** Station name mapper. */
interface StationNameMapper {

    /**
     * Returns the name of the station with [stationUic], or null if the specified
     * station is not found.
     */
    fun stationName(stationUic: Int): String?

}
