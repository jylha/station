package com.example.station.ui.timetable

import com.example.station.model.Station
import com.example.station.model.TimetableRow
import com.example.station.model.Train
import com.example.station.model.Train.Category.LongDistance
import com.google.common.truth.Truth.assertThat
import org.junit.Test


class TimetableViewStateTest {

    private val helsinki = Station(
        true, Station.Type.Station, "Helsinki",
        "HKI", 1, "FI", 1.0, 1.0
    )

    private val trains = listOf(
        Train(1, "S", LongDistance)
    )

    @Test fun `initial state`() {
        val state = TimetableViewState()
        assertThat(state).isNotNull()
        assertThat(state.station).isNull()
        assertThat(state.loading).isFalse()
    }

    @Test fun `reduce state with Loading result`() {
        val state = TimetableViewState(station = null, loading = false)
        val result = state.reduce(TimetableResult.Loading(helsinki))
        assertThat(result.station).isEqualTo(helsinki)
        assertThat(result.loading).isTrue()
    }

    @Test fun `reduce state with Data result`() {
        val state = TimetableViewState(station = helsinki, loading = true, timetable = emptyList())
        val result = state.reduce(TimetableResult.Data(helsinki, trains))
        assertThat(result.station).isEqualTo(helsinki)
        assertThat(result.loading).isFalse()
        assertThat(result.timetable).isEqualTo(trains)
    }

    @Test fun `reduce state with SettingsUpdated result`() {
        val state = TimetableViewState()
        val result = state.reduce(
            TimetableResult.SettingsUpdated(
                setOf(Train.Category.Commuter), setOf(TimetableRow.Type.Arrival)
            )
        )
        assertThat(result.selectedTimetableTypes).containsExactly(TimetableRow.Type.Arrival)
        assertThat(result.selectedTrainCategories).containsExactly(Train.Category.Commuter)
    }

    @Test fun `reduce state with SettingsUpdated result containing only train category`() {
        val state = TimetableViewState(
            selectedTrainCategories = setOf(Train.Category.Commuter),
            selectedTimetableTypes = setOf(TimetableRow.Type.Arrival)
        )
        val result = state.reduce(
            TimetableResult.SettingsUpdated(
                setOf(Train.Category.LongDistance), null
            )
        )
        assertThat(result.selectedTimetableTypes).containsExactly(TimetableRow.Type.Arrival)
        assertThat(result.selectedTrainCategories).containsExactly(Train.Category.LongDistance)
    }

    @Test fun `reduce state with Reloading result`() {
        val state = TimetableViewState(loading = false, reloading = false)
        val result = state.reduce(TimetableResult.Reloading)
        assertThat(result.loading).isFalse()
        assertThat(result.reloading).isTrue()
    }

    @Test fun `reduce state with ReloadedData result`() {
        val state = TimetableViewState(reloading = true, station = station("Pasila", 2),
        timetable = listOf(Train(1, "A", LongDistance)))
        val data = listOf(Train(2, "B", Train.Category.Commuter))
        val result = state.reduce(TimetableResult.ReloadedData(data))
        assertThat(result.reloading).isFalse()
        assertThat(result.station).isEqualTo(station("Pasila", 2))
        assertThat(result.timetable).isEqualTo(data)
    }
}

private fun station(name: String, uic: Int) = Station(
    true, Station.Type.Station, name, "", uic, "FI", 0.0, 0.0
)
