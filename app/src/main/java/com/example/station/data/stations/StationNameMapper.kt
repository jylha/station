package com.example.station.data.stations

import com.example.station.model.Station

/** A functional interface for mapping station UIC to corresponding station name. */
fun interface StationNameMapper {

    /**
     * Returns the name of the station with given [stationCode].
     * @param stationCode UIC code of the station.
     * @return Station name or null if matching station is not found.
     */
    fun stationName(stationCode: Int): String?
}

/** Use station name mapper to rename each station in given [stations] list. */
fun StationNameMapper.rename(stations: List<Station>): List<Station> {
    return stations.map { station ->
        stationName(station.code)?.let { station.copy(name = it) } ?: station
    }
}

/** Renames and sorts stations in alphabetical order. */
fun StationNameMapper.renameAndSort(stations: List<Station>) : List<Station> {
    return rename(stations).sortedBy { it.name }
}
