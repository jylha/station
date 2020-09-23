package com.example.station.ui.timetable

import com.example.station.model.Station
import com.example.station.model.Train
import com.example.station.model.Train.Category.LongDistance
import com.google.common.truth.Truth.assertThat
import org.junit.Test


class TimetableViewStateTest {

    private val helsinki = Station(true, Station.Type.Station, "Helsinki",
    "HKI", 1, "FI", 1.0,1.0)

    private val trains = listOf(
        Train(1, "S", LongDistance )
    )

    @Test fun `initial state`() {
        val state = TimetableViewState()
        assertThat(state).isNotNull()
        assertThat(state.station).isNull()
        assertThat(state.loading).isFalse()
    }

    @Test fun `reduce() with result Loading`() {
        val state  = TimetableViewState(station = null, loading = false)
        val result = state.reduce(TimetableResult.Loading(helsinki))
        assertThat(result.station).isEqualTo(helsinki)
        assertThat(result.loading).isTrue()
    }

    @Test fun `reduce() with result Data`() {
        val state = TimetableViewState(station = helsinki, loading = true, timetable = emptyList())
        val result = state.reduce(TimetableResult.Data(helsinki, trains))
        assertThat(result.station).isEqualTo(helsinki)
        assertThat(result.loading).isFalse()
        assertThat(result.timetable).isEqualTo(trains)
    }
}
