package dev.jylha.station.data.trains

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.get
import dev.jylha.station.data.StationDatabase
import dev.jylha.station.data.trains.network.TrainService
import dev.jylha.station.model.CauseCategory
import dev.jylha.station.model.Station
import dev.jylha.station.model.Train
import dev.jylha.station.util.toCacheEntity
import dev.jylha.station.util.toDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** Train repository that uses Store in orchestrating the data fetching and storing. */
class StoreBackedTrainRepository @Inject constructor(
    private val trainService: TrainService,
    private val stationDatabase: StationDatabase
) : TrainRepository {

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private val categoryStore = StoreBuilder.from(
        fetcher = Fetcher.of { level: Int ->
            withContext(Dispatchers.IO) {
                when (level) {
                    1 -> trainService.fetchCauseCategoryCodes().map { it.toDomainModel() }
                    2 -> trainService.fetchDetailedCauseCategoryCodes().map { it.toDomainModel() }
                    3 -> trainService.fetchThirdLevelCauseCategoryCodes().map { it.toDomainModel() }
                    else -> error("Invalid key.")
                }
            }
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { level ->
                stationDatabase.causeCategoryDao().getCategories(level).map { categories ->
                    categories.map { entity -> entity.toDomainModel() }
                        .ifEmpty { null }
                }
            },
            writer = { level, categories ->
                stationDatabase.causeCategoryDao()
                    .insertAll(categories.map { it.toCacheEntity(level) })
            },
            delete = {},
            deleteAll = null
        )
    ).build()

    override suspend fun train(number: Int, version: Long?): Train? {
        return withContext(Dispatchers.IO) {
            trainService.fetchTrain(number, version).firstOrNull()?.toDomainModel()
        }
    }

    override fun trainsAtStation(stationShortCode: String): Flow<List<Train>>  {
        return flow {
            val trains = trainService.fetchTrainsByCount(
                stationShortCode,
                departed = 1, arrived = 1
            ).toDomainModel()
            emit(trains)
        }.flowOn(Dispatchers.IO)
    }

    override fun trainsAtStation(station: Station): Flow<List<Train>> {
        return trainsAtStation(station.shortCode)
    }

    override suspend fun causeCategories(): List<CauseCategory> {
        return categoryStore.get(1)
    }

    override suspend fun detailedCauseCategories(): List<CauseCategory> {
        return categoryStore.get(2)
    }

    override suspend fun thirdLevelCauseCategories(): List<CauseCategory> {
        return categoryStore.get(3)
    }
}
