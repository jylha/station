package com.example.station.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.staticAmbientOf
import com.example.station.data.stations.StationNameMapper


/**
 * Provider component that provides the StationNameMapper for the AmbientStationNameMapper.
 */
@Composable
fun StationNameProvider(
    stationNameMapper: StationNameMapper?,
    content: @Composable () -> Unit
) {
    val mapper = stationNameMapper ?: StationNameMapper { null }
    Providers(AmbientStationNameMapper provides mapper) {
        content()
    }
}

/**
 * Returns the localised station name for the specified [stationUic] from the StationNameAmbient.
 */
@Composable
fun stationName(stationUic: Int?): String? {
    return when (stationUic) {
        null -> null
        else -> AmbientStationNameMapper.current.stationName(stationUic)
    }
}

/**
 * Ambient to provide [StationNameMapper] instance to allow accessing localised station names.
 */
private val AmbientStationNameMapper = staticAmbientOf<StationNameMapper> {
    error("StationNameMapper in not provided.")
}
