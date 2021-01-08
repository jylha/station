package dev.jylha.station.ui.train

import dev.jylha.station.data.stations.LocalizedStationNames
import dev.jylha.station.model.Train
import com.google.common.truth.Truth.assertThat
import org.junit.Test


class TrainDetailsViewStateTest {

    @Test fun `Initial state`() {
        val result = TrainDetailsViewState.initial()
        assertThat(result.isLoading).isFalse()
        assertThat(result.nameMapper).isNull()
        assertThat(result.train).isNull()
    }

    @Test fun `Reduce state with LoadTrainDetail_Loading result`() {
        val state = TrainDetailsViewState.initial()
        val result = state.reduce(LoadTrainDetails.Loading)
        assertThat(result.isLoading).isTrue()
    }

    @Test fun `Reduce state with LoadTrainDetails_Success result`() {
        val state = TrainDetailsViewState(isLoadingTrain = true)
        val train = Train(1, "A", Train.Category.LongDistance)
        val result = state.reduce(LoadTrainDetails.Success(train))
        assertThat(result.train).isEqualTo(train)
        assertThat(result.isLoading).isFalse()
    }

    @Test fun `Reduce state with LoadTrainDetails_Error result`() {
        val state = TrainDetailsViewState(isLoadingTrain = true)
        val result = state.reduce(LoadTrainDetails.Error(null))
        assertThat(result.isLoading).isFalse()
    }

    @Test fun `Reduce state with ReloadTrainDetails_Loading result`() {
        val state = TrainDetailsViewState(isReloading = false)
        val result = state.reduce(ReloadTrainDetails.Loading)
        assertThat(result.isLoading).isFalse()
        assertThat(result.isReloading).isTrue()
    }

    @Test fun `Reduce state with ReloadTrainDetails_Success result`() {
        val state = TrainDetailsViewState(isReloading = true)
        val train = Train(5, "S", Train.Category.LongDistance)
        val result = state.reduce(ReloadTrainDetails.Success(train))
        assertThat(result.isReloading).isFalse()
        assertThat(result.train).isEqualTo(train)
    }

    @Test fun `Reduce state with ReloadTrainDetails_Error result`() {
        val state = TrainDetailsViewState(isLoadingTrain = true)
        val result = state.reduce(ReloadTrainDetails.Error(null))
        assertThat(result.isReloading).isFalse()
    }
    @Test fun `Reduce state with LoadNameMapper_Loading result`() {
        val state = TrainDetailsViewState.initial()
        val result = state.reduce(LoadNameMapper.Loading)
        assertThat(result.isLoading).isTrue()
    }

    @Test fun `Reduce state with LoadNameMapper_Success result`() {
        val state = TrainDetailsViewState(isLoadingMapper = true)
        val mapper = LocalizedStationNames.from(emptyList())
        val result = state.reduce(LoadNameMapper.Success(mapper))
        assertThat(result.isLoading).isFalse()
        assertThat(result.nameMapper).isEqualTo(mapper)
    }

    @Test fun `Reduce state with LoadNameMapper_Success result while loading train details`() {
        val state = TrainDetailsViewState(isLoadingTrain = true, isLoadingMapper = true)
        val mapper = LocalizedStationNames.from(emptyList())
        val result = state.reduce(LoadNameMapper.Success(mapper))
        assertThat(result.isLoading).isTrue()
        assertThat(result.nameMapper).isEqualTo(mapper)
    }
}
