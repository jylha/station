package dev.jylha.station.ui.train

import dev.jylha.station.data.stations.StationNameMapper
import dev.jylha.station.model.Train

/** Base class for all train detail screen results. */
sealed class TrainDetailsResult

/** Results for loading train details.. */
sealed class LoadTrainDetails : TrainDetailsResult() {
    object Loading : LoadTrainDetails() {
        override fun toString(): String = "LoadTrainDetails.Loading"
    }
    data class Error(val message: String?) : LoadTrainDetails()
    data class Success(val train: Train) : LoadTrainDetails()
}

/** Results for reloading train details. */
sealed class ReloadTrainDetails : TrainDetailsResult() {
    object Loading : ReloadTrainDetails() {
        override fun toString(): String = "ReloadTrainDetails.Loading"
    }
    data class Error(val message: String?) : ReloadTrainDetails()
    data class Success(val train: Train) : ReloadTrainDetails()
}

/** Results for loading station name mapper. */
sealed class LoadNameMapper : TrainDetailsResult() {
    object Loading : LoadNameMapper() {
        override fun toString(): String = "LoadNameMapper.Loading"
    }
    data class Error(val message: String?) : LoadNameMapper()
    data class Success(val mapper: StationNameMapper) : LoadNameMapper()
}
