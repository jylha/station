package com.example.station.ui.train

import androidx.compose.runtime.Immutable
import com.example.station.data.stations.StationNameMapper
import com.example.station.model.Train

@Immutable
data class TrainDetailsViewState constructor(
    private val isLoadingTrain: Boolean = false,
    private val isLoadingMapper: Boolean = false,
    val isReloading: Boolean = false,
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

fun TrainDetailsViewState.reduce(result: TrainDetailsResult): TrainDetailsViewState {
    return when (result) {
        TrainDetailsResult.LoadingTrainDetails -> copy(isLoadingTrain = true)
        is TrainDetailsResult.TrainDetails -> copy(train = result.train, isLoadingTrain = false)
        TrainDetailsResult.LoadingNameMapper -> copy(isLoadingMapper = true)
        is TrainDetailsResult.NameMapper -> copy(
            nameMapper = result.mapper, isLoadingMapper = false
        )
        TrainDetailsResult.ReloadingTrainDetails -> copy(isReloading = true)
        is TrainDetailsResult.TrainDetailsReloaded -> copy(
            isReloading = false,
            train = result.train
        )
    }
}
