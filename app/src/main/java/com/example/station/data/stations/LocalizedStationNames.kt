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
                .map { station -> station.uic to (localizedNames[station.uic] ?: station.name) }
                .toMap()
            return LocalizedStationNames(mapping)
        }

        fun create(stations: List<Station>, context: Context): LocalizedStationNames {
            val localizedNames = LOCALIZED_STATION_NAMES
                .map { (uic, resId) -> uic to context.getString(resId) }
                .toMap()
            return create(stations, localizedNames)
        }
    }

    override fun stationName(stationUic: Int): String? = map[stationUic]
}

private val LOCALIZED_STATION_NAMES = mapOf(
    1 to R.string.station_name_0001_helsinki,
    9 to R.string.station_name_0009_ilmala,
    10 to R.string.station_name_0010_pasila,
    18 to R.string.station_name_0018_tikkurila,
    20 to R.string.station_name_0020_kerava,
    25 to R.string.station_name_0025_jarvenpaa,
    40 to R.string.station_name_0040_riihimaki,
    47 to R.string.station_name_0047_hameenlinna,
    65 to R.string.station_name_0065_kauklahti,
    66 to R.string.station_name_0066_espoo,
    73 to R.string.station_name_0073_hanko,
    130 to R.string.station_name_0130_turku,
    135 to R.string.station_name_0135_turku_harbor,
    160 to R.string.station_name_0160_tampere,
    280 to R.string.station_name_0280_seinajoki,
    351 to R.string.station_name_0351_tornio,
    370 to R.string.station_name_0370_oulu,
    400 to R.string.station_name_0400_pieksamaki,
    408 to R.string.station_name_0408_kuopio,
    413 to R.string.station_name_0413_siilinjarvi,
    460 to R.string.station_name_0460_joensuu,
    480 to R.string.station_name_0480_kouvola,
    492 to R.string.station_name_0492_vainikkala,
    521 to R.string.station_name_0521_savonlinna,
    532 to R.string.station_name_0532_kotka,
    603 to R.string.station_name_0603_imatra,
    623 to R.string.station_name_0623_zoo,
    644 to R.string.station_name_0644_kotka_harbor,
    1328 to R.string.station_name_1328_pasila_car_carrier,

    3181 to R.string.station_name_3181_st_petersburg_lad,
    3820 to R.string.station_name_3820_st_petersburg_fin,
    6007 to R.string.station_name_6007_moscow,
)

