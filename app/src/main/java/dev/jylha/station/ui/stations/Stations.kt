package dev.jylha.station.ui.stations

import androidx.compose.runtime.Immutable
import dev.jylha.station.model.Station

/**
 * An immutable wrapper for a list of stations.
 */
@Immutable
data class Stations(private val stations: List<Station>) : List<Station> by stations
