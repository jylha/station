package dev.jylha.station.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.staticAmbientOf
import dev.jylha.station.data.stations.StationNameMapper

/**
 * Provider component that provides the StationNameMapper for the AmbientStationNameMapper.
 * @param stationNameMapper A station name mapper that provides localised station names.
 * @param content The composable content that can access the provided [StationNameMapper].
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
 * Returns the localised station name for the specified [stationCode] from AmbientStationNameMapper.
 */
@Composable
fun stationName(stationCode: Int?): String? {
    return when (stationCode) {
        null -> null
        else -> AmbientStationNameMapper.current.stationName(stationCode)
    }
}

/**
 * Ambient to provide [StationNameMapper] instance to allow accessing localised station names.
 */
private val AmbientStationNameMapper = staticAmbientOf<StationNameMapper> {
    error("StationNameMapper in not provided.")
}
