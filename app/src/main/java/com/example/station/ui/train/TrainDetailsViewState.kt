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
        LoadTrainDetails.Loading -> copy(isLoadingTrain = true)
        is LoadTrainDetails.Success -> copy(train = result.train, isLoadingTrain = false)
        is LoadTrainDetails.Error -> copy(isLoadingTrain = false)

        ReloadTrainDetails.Loading -> copy(isReloading = true)
        is ReloadTrainDetails.Success -> copy(isReloading = false, train = result.train)
        is ReloadTrainDetails.Error -> copy(isReloading = false)

        LoadNameMapper.Loading -> copy(isLoadingMapper = true)
        is LoadNameMapper.Success -> copy(nameMapper = result.mapper, isLoadingMapper = false)
        is LoadNameMapper.Error -> copy(isLoadingMapper = false)
    }
}
