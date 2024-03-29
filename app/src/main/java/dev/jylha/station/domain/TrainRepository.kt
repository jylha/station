package dev.jylha.station.domain

import dev.jylha.station.model.CauseCategory
import dev.jylha.station.model.Station
import dev.jylha.station.model.Train
import kotlinx.coroutines.flow.Flow

interface TrainRepository {

    /**
     * Returns a train specified by its departure date and number. Returns null, if the train has
     * not been changed since the given [version].
     *
     * @param departureDate Train's departure date (ISO_LOCAL_DATE).
     * @param number Train's number.
     * @param version Version number where train was last updated.
     */
    suspend fun train(departureDate: String, number: Int, version: Long? = null): Train?

    /**
     * Returns a train specified by its number, or null if the train has not been changed
     * since the given [version].
     *
     * @param number Train number.
     * @param version Version number where train was last updated.
     */
    suspend fun latestTrain(number: Int, version: Long? = null): Train?

    /** Returns a list of trains stopping at the specified station. */
    fun trainsAtStation(stationShortCode: String): Flow<List<Train>>

    /** Returns a list of trains stopping at the specified station. */
    fun trainsAtStation(station: Station): Flow<List<Train>>

    /** Returns a list of cause categories for train delays. */
    suspend fun causeCategories(): List<CauseCategory>

    /** Returns a list of detailed cause categories for train delays. */
    suspend fun detailedCauseCategories(): List<CauseCategory>

    /** Returns a list of third level cause categories for train delays. */
    suspend fun thirdLevelCauseCategories(): List<CauseCategory>

}
