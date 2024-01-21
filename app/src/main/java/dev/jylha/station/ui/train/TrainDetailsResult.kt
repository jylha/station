package dev.jylha.station.ui.train

import dev.jylha.station.data.stations.StationNameMapper
import dev.jylha.station.model.Train

/** Base class for all train detail screen results. */
sealed class TrainDetailsResult

/** Results for loading train details.. */
sealed class LoadTrainDetails : TrainDetailsResult() {
    data object Loading : LoadTrainDetails()
    data class Error(val message: String?) : LoadTrainDetails()
    data class Success(val train: Train) : LoadTrainDetails()
}

/** Results for reloading train details. */
sealed class ReloadTrainDetails : TrainDetailsResult() {
    data object Loading : ReloadTrainDetails()
    data class Error(val message: String?) : ReloadTrainDetails()
    data class Success(val train: Train) : ReloadTrainDetails()
}

/** Results for loading station name mapper. */
sealed class LoadNameMapper : TrainDetailsResult() {
    data object Loading : LoadNameMapper()
    data class Error(val message: String?) : LoadNameMapper()
    data class Success(val mapper: StationNameMapper) : LoadNameMapper()
}
