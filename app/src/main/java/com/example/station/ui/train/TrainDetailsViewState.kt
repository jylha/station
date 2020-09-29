package com.example.station.ui.train

import androidx.compose.runtime.Immutable
import com.example.station.data.stations.StationNameMapper
import com.example.station.model.Train

@Immutable
data class TrainDetailsViewState constructor(
    val loading: Boolean = false,
    val train: Train? = null,
    val nameMapper: StationNameMapper? = null,
) {
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
        is TrainDetailsViewResult.NameMapper -> {
            copy(nameMapper = result.mapper)
        }
        is TrainDetailsViewResult.TrainDetails -> {
            copy(train = result.train)
        }
    }
}
