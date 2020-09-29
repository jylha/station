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
        assertThat(result.loading).isFalse()
        assertThat(result.nameMapper).isNull()
        assertThat(result.train).isNull()
    }

    @Test fun `Reduce state with NameMapper result`() {
        val state = TrainDetailsViewState.initial()
        val mapper = LocalizedStationNames.create(emptyList())
        val result = state.reduce(NameMapper(mapper))
        assertThat(result.nameMapper).isEqualTo(mapper)
    }

    @Test fun `Reduce state with TrainDetails result`() {
        val state = TrainDetailsViewState.initial()
        val train = Train(1, "A", Train.Category.LongDistance)
        val result = state.reduce(TrainDetails(train))
        assertThat(result.train).isEqualTo(train)
    }
}
