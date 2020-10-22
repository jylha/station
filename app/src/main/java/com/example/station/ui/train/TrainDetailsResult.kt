package com.example.station.ui.train

import com.example.station.data.stations.StationNameMapper
import com.example.station.model.Train

/** Base class for all train detail screen results. */
sealed class TrainDetailsResult

/** Results for loading train details.. */
sealed class LoadTrainDetails : TrainDetailsResult() {
    object Loading : LoadTrainDetails()
    data class Error(val message: String?) : LoadTrainDetails()
    data class Success(val train: Train) : LoadTrainDetails()
}

/** Results for reloading train details. */
sealed class ReloadTrainDetails : TrainDetailsResult() {
    object Loading : ReloadTrainDetails()
    data class Error(val message: String?) : ReloadTrainDetails()
    data class Success(val train: Train) : ReloadTrainDetails()
}

/** Results for loading station name mapper. */
sealed class LoadNameMapper : TrainDetailsResult() {
    object Loading : LoadNameMapper()
    data class Error(val message: String?) : LoadNameMapper()
    data class Success(val mapper: StationNameMapper) : LoadNameMapper()
}
