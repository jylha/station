package com.example.station.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.ambientOf
import androidx.compose.runtime.remember
import com.example.station.data.stations.StationNameMapper

@Composable
fun StationNameProvider(nameMapper: StationNameMapper?, content: @Composable () -> Unit) {
    val mapper = remember(nameMapper) {
        nameMapper ?: object : StationNameMapper {
            override fun stationName(stationUic: Int): String? = null
            override fun stationName(stationShortCode: String): String? = null
        }
    }
    Providers(StationNameAmbient provides mapper) {
        content()
    }
}

object StationName {
    @Composable
    fun forUic(stationUic: Int) = StationNameAmbient.current.stationName(stationUic)
}

private val StationNameAmbient = ambientOf<StationNameMapper> {
    error("StationNameMapper in not provided.")
}
