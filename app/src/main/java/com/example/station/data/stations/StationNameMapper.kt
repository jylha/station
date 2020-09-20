package com.example.station.data.stations

/** Station name mapper. */
interface StationNameMapper {

    /** Returns the localized commercial name of the station with [stationUic]. */
    fun stationName(stationUic: Int): String?

}
