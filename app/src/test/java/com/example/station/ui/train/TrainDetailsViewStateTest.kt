package com.example.station.ui.train

import com.example.station.data.stations.LocalizedStationNames
import com.example.station.model.Train
import com.example.station.ui.train.TrainDetailsResult.NameMapper
import com.example.station.ui.train.TrainDetailsResult.TrainDetails
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
        val result = state.reduce(TrainDetailsResult.LoadingNameMapper)
        assertThat(result.isLoading).isTrue()
    }

    @Test fun `Reduce state with NameMapper result`() {
        val state = TrainDetailsViewState(isLoadingMapper = true)
        val mapper = LocalizedStationNames.from(emptyList())
        val result = state.reduce(NameMapper(mapper))
        assertThat(result.isLoading).isFalse()
        assertThat(result.nameMapper).isEqualTo(mapper)
    }

    @Test fun `Reduce state with NameMapper result while loading train details`() {
        val state = TrainDetailsViewState(isLoadingTrain = true, isLoadingMapper = true)
        val mapper = LocalizedStationNames.from(emptyList())
        val result = state.reduce(NameMapper(mapper))
        assertThat(result.isLoading).isTrue()
        assertThat(result.nameMapper).isEqualTo(mapper)
    }

    @Test fun `Reduce state with LoadingTrainDetail result`() {
        val state = TrainDetailsViewState.initial()
        val result = state.reduce(TrainDetailsResult.LoadingTrainDetails)
        assertThat(result.isLoading).isTrue()
    }

    @Test fun `Reduce state with TrainDetails result`() {
        val state = TrainDetailsViewState(isLoadingTrain = true)
        val train = Train(1, "A", Train.Category.LongDistance)
        val result = state.reduce(TrainDetails(train))
        assertThat(result.train).isEqualTo(train)
        assertThat(result.isLoading).isFalse()
    }

    @Test fun `Reduce state with ReloadTrainDetails result`() {
        val state = TrainDetailsViewState(isReloading = false)
        val result = state.reduce(TrainDetailsResult.ReloadingTrainDetails)
        assertThat(result.isLoading).isFalse()
        assertThat(result.isReloading).isTrue()
    }

    @Test fun `Reduce state with TrainDetailsReloaded result`() {
        val state = TrainDetailsViewState(isReloading = true)
        val train = Train(5, "S", Train.Category.LongDistance)
        val result = state.reduce(TrainDetailsResult.TrainDetailsReloaded(train))
        assertThat(result.isReloading).isFalse()
        assertThat(result.train).isEqualTo(train)
    }
}
