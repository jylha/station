package com.example.station.ui.train

import androidx.compose.runtime.Immutable
import com.example.station.data.stations.StationNameMapper
import com.example.station.model.Train

@Immutable
data class TrainDetailsViewState constructor(
    private val isLoadingTrain: Boolean = false,
    private val isLoadingMapper: Boolean = false,
    val train: Train? = null,
    val nameMapper: StationNameMapper? = null,
) {
    val isLoading: Boolean
        get() = isLoadingTrain || isLoadingMapper

    companion object {
        fun initial(): TrainDetailsViewState {
            return TrainDetailsViewState(
                nameMapper = null
            )
        }
    }
}

fun TrainDetailsViewState.reduce(result: TrainDetailsViewResult): TrainDetailsViewState {
    return when (result) {
        TrainDetailsViewResult.LoadingTrainDetails -> copy(isLoadingTrain = true)
        is TrainDetailsViewResult.TrainDetails -> copy(train = result.train, isLoadingTrain = false)
        TrainDetailsViewResult.LoadingNameMapper -> copy(isLoadingMapper = true)
        is TrainDetailsViewResult.NameMapper -> copy(
            nameMapper = result.mapper, isLoadingMapper = false
        )
    }
}
