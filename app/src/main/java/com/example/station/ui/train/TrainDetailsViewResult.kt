package com.example.station.ui.train

import com.example.station.data.stations.StationNameMapper
import com.example.station.model.Train

sealed class TrainDetailsViewResult {
    data class TrainDetails(val train: Train): TrainDetailsViewResult()
    data class NameMapper(val mapper: StationNameMapper) : TrainDetailsViewResult()
}
