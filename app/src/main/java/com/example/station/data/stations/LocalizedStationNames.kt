package com.example.station.data.stations

import android.content.Context
import com.example.station.R
import com.example.station.model.Station

/**
 * Station name mapper that provides station names for each station.
 */
class LocalizedStationNames private constructor(
    val map: Map<Int, String>
) : StationNameMapper {

    companion object {
        /**
         * Creates station name mapping from [stations]. When an alternative name for station is
         * given in [localizedNames], it will be used in the mapping.
         */
        fun from(
            stations: List<Station>,
            localizedNames: Map<Int, String> = emptyMap()
        ): LocalizedStationNames {
            val mapping = stations
                .map { station -> station.uic to (localizedNames[station.uic] ?: station.name) }
                .toMap()
            return LocalizedStationNames(mapping)
        }

        /**
         * Creates station name mapping from [stations]. This overload uses a replaces the name of
         * of every station that requires either a localized or a commercial version of its name.
         */
        fun from(stations: List<Station>, context: Context): LocalizedStationNames {
            val localizedNames = LOCALIZED_STATION_NAMES
                .map { (uic, resId) -> uic to context.getString(resId) }
                .toMap()
            return from(stations, localizedNames)
        }
    }

    override fun stationName(stationUic: Int): String? = map[stationUic]
}

/**
 * A map from station UICs to station names for those stations that require either a localized or
 * a commercial version of their name.
 */
private val LOCALIZED_STATION_NAMES = mapOf(
    1 to R.string.station_name_0001_helsinki,
    9 to R.string.station_name_0009_ilmala,
    10 to R.string.station_name_0010_pasila,
    15 to R.string.station_name_0015_oulunkyla,
    17 to R.string.station_name_0017_malmi,
    18 to R.string.station_name_0018_tikkurila,
    20 to R.string.station_name_0020_kerava,
    22 to R.string.station_name_0022_nikkila,
    23 to R.string.station_name_0023_porvoo,
    25 to R.string.station_name_0025_jarvenpaa,
    30 to R.string.station_name_0030_hyvinkaa,
    40 to R.string.station_name_0040_riihimaki,
    47 to R.string.station_name_0047_hameenlinna,
    60 to R.string.station_name_0060_karjaa,
    63 to R.string.station_name_0063_kirkkonummi,
    64 to R.string.station_name_0064_masala,
    65 to R.string.station_name_0065_kauklahti,
    66 to R.string.station_name_0066_espoo,
    67 to R.string.station_name_0067_kauniainen,
    68 to R.string.station_name_0068_leppavaara,
    69 to R.string.station_name_0069_pitajanmaki,
    72 to R.string.station_name_0072_huopalahti,
    73 to R.string.station_name_0073_hanko,
    75 to R.string.station_name_0075_lappohja,
    76 to R.string.station_name_0076_tammisaari,
    100 to R.string.station_name_0100_lahti,
    121 to R.string.station_name_0121_uusikaupunki,
    124 to R.string.station_name_0124_naantali,
    126 to R.string.station_name_0126_kupittaa,
    130 to R.string.station_name_0130_turku,
    135 to R.string.station_name_0135_turku_harbor,
    160 to R.string.station_name_0160_tampere,
    170 to R.string.station_name_0170_kokemaki,
    220 to R.string.station_name_0220_pori,
    280 to R.string.station_name_0280_seinajoki,
    288 to R.string.station_name_0288_vaasa,
    293 to R.string.station_name_0293_laihia,
    295 to R.string.station_name_0295_isokyro,
    298 to R.string.station_name_0298_lapua,
    305 to R.string.station_name_0305_pietarsaari,
    311 to R.string.station_name_0311_kruunupyy,
    312 to R.string.station_name_0312_kokkola,
    351 to R.string.station_name_0351_tornio,
    370 to R.string.station_name_0370_oulu,
    387 to R.string.station_name_0387_kajaani,
    400 to R.string.station_name_0400_pieksamaki,
    408 to R.string.station_name_0408_kuopio,
    413 to R.string.station_name_0413_siilinjarvi,
    420 to R.string.station_name_0420_iisalmi,
    460 to R.string.station_name_0460_joensuu,
    480 to R.string.station_name_0480_kouvola,
    492 to R.string.station_name_0492_vainikkala,
    495 to R.string.station_name_0495_lappeenranta,
    521 to R.string.station_name_0521_savonlinna,
    532 to R.string.station_name_0532_kotka,
    534 to R.string.station_name_0534_kymi,
    546 to R.string.station_name_0546_mikkeli,
    551 to R.string.station_name_0551_pukinmaki,
    552 to R.string.station_name_0552_tapanila,
    553 to R.string.station_name_0553_puistola,
    554 to R.string.station_name_0554_rekola,
    556 to R.string.station_name_0556_hiekkaharju,
    559 to R.string.station_name_0559_koivukyla,
    561 to R.string.station_name_0561_hinthaara,
    576 to R.string.station_name_0576_siuntio,
    579 to R.string.station_name_0579_tuomarila,
    603 to R.string.station_name_0603_imatra,
    623 to R.string.station_name_0623_zoo,
    644 to R.string.station_name_0644_kotka_harbor,
    657 to R.string.station_name_0657_pohjois_haaga,
    658 to R.string.station_name_0658_kannelmaki,
    659 to R.string.station_name_0659_malminkartano,
    660 to R.string.station_name_0660_myyrmaki,
    661 to R.string.station_name_0661_louhela,
    662 to R.string.station_name_0662_martinlaakso,
    675 to R.string.station_name_0675_koivuhovi,
    789 to R.string.station_name_0789_ylitornio,
    827 to R.string.station_name_0827_santala,
    830 to R.string.station_name_0830_tolsa,
    839 to R.string.station_name_0839_vantaankoski,
    847 to R.string.station_name_0847_valimo,
    879 to R.string.station_name_0879_hanko_pohjoinen,
    977 to R.string.station_name_0977_kapyla,
    1015 to R.string.station_name_1015_haksi,
    1113 to R.string.station_name_1113_kiiala,
    1318 to R.string.station_name_1318_tornio_ita,
    1328 to R.string.station_name_1328_pasila_car_carrier,
    1332 to R.string.station_name_1332_airport,
    1333 to R.string.station_name_1333_leinela,
    1337 to R.string.station_name_1337_vehkala,

    3181 to R.string.station_name_3181_st_petersburg_lad,
    3820 to R.string.station_name_3820_st_petersburg_fin,
    6007 to R.string.station_name_6007_moscow,
)

