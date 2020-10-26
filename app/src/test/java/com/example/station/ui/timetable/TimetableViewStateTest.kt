package com.example.station.ui.timetable

import com.example.station.data.stations.StationNameMapper
import com.example.station.model.CauseCategories
import com.example.station.model.CauseCategory
import com.example.station.model.Station
import com.example.station.model.TimetableRow
import com.example.station.model.Train
import com.example.station.model.Train.Category.LongDistance
import com.google.common.truth.Truth.assertThat
import org.junit.Test


class TimetableViewStateTest {

    private val helsinki = Station("Helsinki", "HKI", 1, 1.0, 1.0)

    private val timetable = listOf(
        Train(1, "S", LongDistance)
    )

    @Test fun `initial state`() {
        val state = TimetableViewState()
        assertThat(state).isNotNull()
        assertThat(state.station).isNull()
        assertThat(state.isLoadingTimetable).isFalse()
    }

    @Test fun `reduce state with LoadTimetable_Loading result`() {
        val state = TimetableViewState(
            station = null, isLoadingTimetable = false, timetable = timetable,
            loadingTimetableFailed = true
        )
        val result = state.reduce(LoadTimetable.Loading(helsinki))
        assertThat(result.station).isEqualTo(helsinki)
        assertThat(result.isLoadingTimetable).isTrue()
        assertThat(result.loadingTimetableFailed).isFalse()
        assertThat(result.timetable).isEmpty()
    }

    @Test fun `reduce state with LoadTimetable_Success result`() {
        val state = TimetableViewState(
            station = helsinki,
            isLoadingTimetable = true,
            loadingTimetableFailed = true,
            timetable = emptyList()
        )
        val result = state.reduce(LoadTimetable.Success(helsinki, timetable))
        assertThat(result.station).isEqualTo(helsinki)
        assertThat(result.isLoadingTimetable).isFalse()
        assertThat(result.loadingTimetableFailed).isFalse()
        assertThat(result.timetable).isEqualTo(timetable)
    }

    @Test fun `reduce state with LoadTimetable_Error result`() {
        val state = TimetableViewState(
            station = helsinki,
            isLoadingTimetable = true,
            loadingTimetableFailed = false,
            timetable = timetable
        )
        val message = "Oops. Error happened."
        val result = state.reduce(LoadTimetable.Error(message))
        assertThat(result.station).isEqualTo(helsinki)
        assertThat(result.timetable).isEmpty()
        assertThat(result.isLoadingTimetable).isFalse()
        assertThat(result.loadingTimetableFailed).isTrue()
    }

    @Test fun `reduce state with SettingsUpdated result`() {
        val state = TimetableViewState()
        val result = state.reduce(
            SettingsUpdated(
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
            SettingsUpdated(
                setOf(Train.Category.LongDistance), null
            )
        )
        assertThat(result.selectedTimetableTypes).containsExactly(TimetableRow.Type.Arrival)
        assertThat(result.selectedTrainCategories).containsExactly(Train.Category.LongDistance)
    }

    @Test fun `reduce state with ReloadTimetable_Loading result`() {
        val state = TimetableViewState(isLoadingTimetable = false, isReloadingTimetable = false)
        val result = state.reduce(ReloadTimetable.Loading)
        assertThat(result.isLoadingTimetable).isFalse()
        assertThat(result.isReloadingTimetable).isTrue()
    }

    @Test fun `reduce state with ReloadTimetable_Success result`() {
        val state = TimetableViewState(
            isReloadingTimetable = true, station = station("Pasila", 2),
            timetable = listOf(Train(1, "A", LongDistance))
        )
        val timetable = listOf(Train(2, "B", Train.Category.Commuter))
        val result = state.reduce(ReloadTimetable.Success(timetable))
        assertThat(result.isReloadingTimetable).isFalse()
        assertThat(result.station).isEqualTo(station("Pasila", 2))
        assertThat(result.timetable).isEqualTo(timetable)
    }

    @Test fun `reduce state with ReloadTimetable_Error result`() {
        val state = TimetableViewState(
            isReloadingTimetable = true, station = station("Pasila", 2),
            timetable = listOf(Train(1, "A", LongDistance))
        )
        val message = "Error!"
        val result = state.reduce(ReloadTimetable.Error(message))
        assertThat(result.isReloadingTimetable).isFalse()
        assertThat(result.errorMessage).isEqualTo(message)
    }

    @Test fun `reduce state with LoadStationNames_Loading result`() {
        val state = TimetableViewState(isLoadingStationNames = false)
        val expected = state.copy(isLoadingStationNames = true)
        val result = state.reduce(LoadStationNames.Loading)
        assertThat(result).isEqualTo(expected)
    }

    @Test fun `reduce state with LoadStationNames_Success result`() {
        val state = TimetableViewState(isLoadingStationNames = true, stationNameMapper = null)
        val mapper = object : StationNameMapper {
            override fun stationName(stationUic: Int): String? = "Station $stationUic"
        }
        val result = state.reduce(LoadStationNames.Success(mapper))
        assertThat(result.isLoadingStationNames).isFalse()
        assertThat(result.stationNameMapper).isEqualTo(mapper)
    }

    @Test fun `reduce state with LoadCauseCategories_Loading result`() {
        val state = TimetableViewState(isLoadingCauseCategories = false)
        val result = state.reduce(LoadCauseCategories.Loading)
        assertThat(result.isLoadingCauseCategories).isTrue()
    }

    @Test fun `reduce state with LoadCauseCategories_Success result`() {
        val state = TimetableViewState(isLoadingCauseCategories = true, causeCategories = null)
        val categories = listOf(CauseCategory(1, "First"))
        val detailedCategories = listOf(CauseCategory(2, "Second"))
        val causeCategories = CauseCategories(categories, detailedCategories)
        val result = state.reduce(LoadCauseCategories.Success(causeCategories))
        assertThat(result.isLoadingCauseCategories).isFalse()
        assertThat(result.causeCategories).isEqualTo(causeCategories)
    }

    @Test fun `reduce state with LoadCauseCategories_Error result`() {
        val state = TimetableViewState(isLoadingCauseCategories = true, errorMessage = null)
        val message = "Something."
        val result = state.reduce(LoadCauseCategories.Error(message))
        assertThat(result.isLoadingCauseCategories).isFalse()
        assertThat(result.errorMessage).isEqualTo(message)
    }
}

private fun station(name: String, uic: Int) = Station(
    true, Station.Type.Station, name, "", uic, "FI", 0.0, 0.0
)
