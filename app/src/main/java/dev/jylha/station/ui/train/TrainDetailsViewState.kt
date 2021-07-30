package dev.jylha.station.ui.train

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import dev.jylha.station.data.stations.StationNameMapper
import dev.jylha.station.model.Train

@Immutable
data class TrainDetailsViewState constructor(
    private val isLoadingTrain: Boolean = false,
    private val isLoadingMapper: Boolean = false,
    val isReloading: Boolean = false,
    val train: Train? = null,
    val nameMapper: StationNameMapper? = null,
) {
    val isLoading: Boolean
        @Stable get() = isLoadingTrain || isLoadingMapper

    fun reduce(result: TrainDetailsResult): TrainDetailsViewState {
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

    companion object {
        val initial: TrainDetailsViewState = TrainDetailsViewState(nameMapper = null)
    }
}
