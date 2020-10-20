package com.example.station.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.ambientOf
import androidx.compose.runtime.remember
import com.example.station.data.stations.StationNameMapper


/**
 * Provider component that provides the StationNameMapper for the StationNameAmbient.
.*/
@Composable
fun StationNameProvider(
    nameMapper: StationNameMapper?,
    content: @Composable () -> Unit
) {
    val mapper = remember(nameMapper) {
        nameMapper ?: object : StationNameMapper {
            override fun stationName(stationUic: Int): String? = null
        }
    }
    Providers(StationNameAmbient provides mapper) {
        content()
    }
}

/**
 * Returns the localised station name for the specified [stationUic] from the StationNameAmbient.
 */
@Composable
fun stationName(stationUic: Int?): String? {
    return if (stationUic != null) StationNameAmbient.current.stationName(stationUic) else null
}

/**
 * Ambient to provide [StationNameMapper] instance to allow accessing localised station names.
 */
private val StationNameAmbient = ambientOf<StationNameMapper> {
    error("StationNameMapper in not provided.")
}
