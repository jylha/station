package com.example.station.ui.train

import com.example.station.data.stations.LocalizedStationNames
import com.example.station.model.Train
import com.example.station.ui.train.TrainDetailsViewResult.NameMapper
import com.example.station.ui.train.TrainDetailsViewResult.TrainDetails
import com.google.common.truth.Truth.assertThat
import org.junit.Test


class TrainDetailsViewStateTest {

    @Test fun `Initial state`() {
        val result = TrainDetailsViewState.initial()
        assertThat(result.isLoading).isFalse()
        assertThat(result.nameMapper).isNull()
        assertThat(result.train).isNull()
    }

    @Test fun `Reduce state with LoadingNameMapper result`() {
        val state = TrainDetailsViewState.initial()
        val result = state.reduce(TrainDetailsViewResult.LoadingNameMapper)
        assertThat(result.isLoading).isTrue()
    }

    @Test fun `Reduce state with NameMapper result`() {
        val state = TrainDetailsViewState(isLoadingMapper = true)
        val mapper = LocalizedStationNames.create(emptyList())
        val result = state.reduce(NameMapper(mapper))
        assertThat(result.isLoading).isFalse()
        assertThat(result.nameMapper).isEqualTo(mapper)
    }

    @Test fun `Reduce state with NameMapper result while loading train details`() {
        val state = TrainDetailsViewState(isLoadingTrain = true, isLoadingMapper = true)
        val mapper = LocalizedStationNames.create(emptyList())
        val result = state.reduce(NameMapper(mapper))
        assertThat(result.isLoading).isTrue()
        assertThat(result.nameMapper).isEqualTo(mapper)
    }

    @Test fun `Reduce state with LoadingTrainDetail result`() {
        val state = TrainDetailsViewState.initial()
        val result = state.reduce(TrainDetailsViewResult.LoadingTrainDetails)
        assertThat(result.isLoading).isTrue()
    }

    @Test fun `Reduce state with TrainDetails result`() {
        val state = TrainDetailsViewState(isLoadingTrain = true)
        val train = Train(1, "A", Train.Category.LongDistance)
        val result = state.reduce(TrainDetails(train))
        assertThat(result.train).isEqualTo(train)
        assertThat(result.isLoading).isFalse()
    }
}
