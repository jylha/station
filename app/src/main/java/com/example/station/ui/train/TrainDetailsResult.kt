package com.example.station.ui.train

import com.example.station.data.stations.StationNameMapper
import com.example.station.model.Train

sealed class TrainDetailsResult {
    object LoadingTrainDetails: TrainDetailsResult()
    data class TrainDetails(val train: Train): TrainDetailsResult()
    object LoadingNameMapper : TrainDetailsResult()
    data class NameMapper(val mapper: StationNameMapper) : TrainDetailsResult()
    object ReloadingTrainDetails: TrainDetailsResult()
    data class TrainDetailsReloaded(val train: Train): TrainDetailsResult()

}
