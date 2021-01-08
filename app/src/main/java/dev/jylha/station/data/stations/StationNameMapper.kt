package dev.jylha.station.data.stations

import dev.jylha.station.model.Station

/** A functional interface for mapping station UIC code to corresponding station name. */
fun interface StationNameMapper {

    /**
     * Returns the name of the station with given [stationCode].
     * @param stationCode UIC code of the station.
     * @return Station name or null if matching station is not found.
     */
    fun stationName(stationCode: Int): String?
}

/**
 *  Uses station name mapper to rename stations in the given [stations] list.
 *  @receiver An implementation of [StationNameMapper] interface.
 *  @param stations A list of stations.
 *  @return A list of given stations with renamed station names.
 */
fun StationNameMapper.rename(stations: List<Station>): List<Station> {
    return stations.map { station ->
        stationName(station.code)?.let { station.copy(name = it) } ?: station
    }
}

/**
 * Uses station name mapper to rename stations in the given [stations] list and then sorts them
 * into alphabetical order by station names.
 * @receiver An implementation of [StationNameMapper] interface.
 * @param stations A list of stations.
 * @return A list of given stations with renamed stations names and sorted by names.
 */
fun StationNameMapper.renameAndSort(stations: List<Station>) : List<Station> {
    return rename(stations).sortedBy { it.name }
}
