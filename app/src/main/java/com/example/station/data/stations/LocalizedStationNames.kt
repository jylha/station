package com.example.station.data.stations

import android.content.Context
import com.example.station.R
import com.example.station.model.Station

/** Station name mapping. */
class LocalizedStationNames private constructor(
    val map: Map<Int, String>
) : StationNameMapper {

    companion object {
        /**
         * Creates station name mapping from [stations]. Station name will be overridden by
         * localized version from [localizedNames], when such is present.
         */
        fun create(
            stations: List<Station>,
            localizedNames: Map<Int, String> = emptyMap()
        ): LocalizedStationNames {
            val mapping = stations
                .map { station ->
                    station.uic to (localizedNames[station.uic] ?: station.name)
                }.toMap()
            return LocalizedStationNames(mapping)
        }

        fun create(stations: List<Station>, context: Context): LocalizedStationNames {
            val localizedNames = LOCALIZED_STATION_NAMES
                .map { (uic, resId) -> uic to context.getString(resId) }
                .toMap()
            return create(stations, localizedNames)
        }
    }

    override fun stationName(stationUic: Int): String? {
        return map[stationUic]
    }

    override fun stationName(stationShortCode: String): String? {
        TODO("Not yet implemented")
    }
}

private val LOCALIZED_STATION_NAMES = mapOf(
    1 to R.string.station_name_helsinki,
    18 to R.string.station_name_tikkurila,
    160 to R.string.station_name_tampere
)

