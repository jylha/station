package com.example.station.ui.timetable

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTesting
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertLabelEquals
import androidx.compose.ui.test.hasSubstring
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithLabel
import androidx.compose.ui.test.onNodeWithSubstring
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.station.data.stations.LocalizedStationNames
import com.example.station.model.Station
import com.example.station.model.TimetableRow
import com.example.station.model.Train
import com.example.station.model.arrival
import com.example.station.model.departure
import com.example.station.testutil.at
import com.example.station.testutil.setThemedContent
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.Rule
import org.junit.Test

private const val LABEL_SELECT_STATION = "Select station"
private const val LABEL_SHOW_FILTERS = "Show filters"
private const val LABEL_HIDE_FILTERS = "Hide filters"
private const val LABEL_SHOW_DEPARTING_TRAINS = "Show departing trains"
private const val LABEL_HIDE_DEPARTING_TRAINS = "Hide departing trains"
private const val LABEL_SHOW_ARRIVING_TRAINS = "Show arriving trains"
private const val LABEL_HIDE_ARRIVING_TRAINS = "Hide arriving trains"
private const val LABEL_SHOW_COMMUTER_TRAINS = "Show commuter trains"
private const val LABEL_HIDE_COMMUTER_TRAINS = "Hide commuter trains"
private const val LABEL_SHOW_LONG_DISTANCE_TRAINS = "Show long-distance trains"
private const val LABEL_HIDE_LONG_DISTANCE_TRAINS = "Hide long-distance trains"

private const val TEXT_COMMUTER = "Commuter"
private const val TEXT_LONG_DISTANCE = "Long-distance"
private const val TEXT_ARRIVING = "Arriving"
private const val TEXT_DEPARTING = "Departing"
private const val TEXT_ALL_TRAINS = "All trains"
private const val TEXT_ARRIVING_TRAINS = "Arriving trains"
private const val TEXT_DEPARTING_TRAINS = "Departing trains"
private const val TEXT_COMMUTER_TRAINS = "Commuter trains"
private const val TEXT_LONG_DISTANCE_TRAINS = "Long-distance trains"

@OptIn(ExperimentalTesting::class)
class TimetableScreenTest {

    @get:Rule val rule = createComposeRule()

    private val helsinki = Station("Helsinki", "HKI", 1, 24.941249, 60.172097)
    private val pasila = Station("Pasila", "PSL", 10, 24.933521, 60.198689)
    private val tikkurila = Station("Tikkurila", "TKL", 18, 25.291811, 64.771091)
    private val testStations = listOf(helsinki, pasila, tikkurila)
    private val testStationMapper = LocalizedStationNames.from(testStations)

    @Test fun loadingTimetable() {
        val state = TimetableViewState(isLoadingTimetable = true)
        rule.clockTestRule.pauseClock()
        rule.setThemedContent { TimetableScreen(viewState = state) }
        rule.clockTestRule.advanceClock(100)

        rule.onNodeWithText("Retrieving timetable.").assertIsDisplayed()
    }

    @Test fun emptyTimetable() {
        val state = TimetableViewState(station = pasila, stationNameMapper = testStationMapper)
        rule.setThemedContent { TimetableScreen(viewState = state) }

        rule.onNodeWithText("Pasila").assertIsDisplayed()
        rule.onNodeWithText(TEXT_ALL_TRAINS).assertIsDisplayed()
        rule.onNodeWithText("No trains are scheduled to stop at this station in the near future.")
            .assertIsDisplayed()
    }

    private val trains = listOf(
        Train(
            1, "IC", Train.Category.LongDistance, timetable = listOf(
                departure(
                    1, "1", at("12:00"), actualTime = at("12:00"),
                    differenceInMinutes = 0, markedReady = true
                ),
                arrival(
                    10, "2", at("12:10"), actualTime = at("12:12"),
                    differenceInMinutes = 2
                ),
                departure(10, "2", at("12:15")),
                arrival(18, "1", at("12:25"))
            )
        ),
        Train(
            2, "S", Train.Category.LongDistance, timetable = listOf(
                departure(10, "1", at("12:45")),
                arrival(1, "4", at("13:00"))
            )
        ),
        Train(
            3, "ABC", Train.Category.Commuter, commuterLineId = "Z", timetable = listOf(
                departure(1, "5", at("12:30")),
                arrival(
                    10, "3", at("12:45"), estimatedTime = at("12:48"),
                    differenceInMinutes = 3
                )
            )
        )
    )

    @Test fun timetable() {
        val state = TimetableViewState(
            station = pasila, timetable = trains, stationNameMapper = testStationMapper
        )
        rule.setThemedContent { TimetableScreen(viewState = state) }

        rule.onNodeWithText(TEXT_ALL_TRAINS).assertIsDisplayed()
        rule.onNodeWithLabel(LABEL_SELECT_STATION).assertIsDisplayed()
        rule.onNodeWithLabel(LABEL_SHOW_FILTERS).assertIsDisplayed()
        rule.onNodeWithLabel(LABEL_HIDE_FILTERS).assertDoesNotExist()

        rule.onNodeWithSubstring("IC, 1").assertIsDisplayed()
            .assert(hasSubstring("Helsinki"))
            .assert(hasSubstring("Tikkurila"))
            .assert(hasSubstring("ARRIVED, 12:12, +2"))
            .assert(hasSubstring("TRACK, 2"))
            .assert(hasSubstring("DEPARTS, 12:15"))
            .assertLabelEquals(
                "Intercity train 1, " +
                        "From Helsinki, " +
                        "To Tikkurila, " +
                        "Arrived at 12:12, " +
                        "To track 2, " +
                        "Departs at 12:15"
            )

        rule.onNodeWithSubstring("S, 2").assertIsDisplayed()
            .assert(hasSubstring("Pasila"))
            .assert(hasSubstring("Helsinki"))
            .assert(hasSubstring("TRACK, 1"))
            .assert(hasSubstring("DEPARTS, 12:45"))
            .assertLabelEquals(
                "Pendolino train 2, " +
                        "From Pasila, " +
                        "To Helsinki, " +
                        "To track 1, " +
                        "Departs at 12:45"
            )

        rule.onNodeWithSubstring("TRACK, 3").assertIsDisplayed()
            .assert(hasSubstring("Helsinki"))
            .assert(hasSubstring("Pasila"))
            .assert(hasSubstring("ARRIVES, 12:45, 12:48"))
            .assert(hasSubstring("Z"))
            .assertLabelEquals(
                "Z commuter train, " +
                        "From Helsinki, " +
                        "To Pasila, " +
                        "Estimated time of arrival 12:48, " +
                        "To track 3"
            )
    }

    @Test fun changeTrainCategoryFromCommuterToLongDistance() {
        val viewState = TimetableViewState(
            station = helsinki, timetable = trains, stationNameMapper = testStationMapper,
            selectedTrainCategories = setOf(Train.Category.Commuter)
        )
        val onTimetableEvent = mock<(TimetableEvent) -> Unit>()
        rule.setThemedContent {
            var state by remember(viewState) { mutableStateOf(viewState) }
            TimetableScreen(viewState = state, onEvent = { event: TimetableEvent ->
                    if (event is TimetableEvent.SelectCategories)
                        state = state.copy(selectedTrainCategories = event.categories)
                    onTimetableEvent(event)
                })
            }

        rule.onNodeWithText(TEXT_ALL_TRAINS).assertDoesNotExist()
        rule.onNodeWithText(TEXT_COMMUTER_TRAINS).assertIsDisplayed()
        rule.onNodeWithText(TEXT_LONG_DISTANCE_TRAINS).assertDoesNotExist()
        rule.onNodeWithLabel(LABEL_HIDE_FILTERS).assertDoesNotExist()
        rule.onNodeWithLabel(LABEL_SHOW_FILTERS).assertIsDisplayed()

        // Show filters
        rule.onNodeWithLabel(LABEL_SHOW_FILTERS).performClick()

        rule.onNodeWithLabel(LABEL_SHOW_FILTERS).assertDoesNotExist()
        rule.onNodeWithLabel(LABEL_HIDE_FILTERS).assertIsDisplayed()

        rule.onNodeWithText(TEXT_LONG_DISTANCE).assertLabelEquals(LABEL_SHOW_LONG_DISTANCE_TRAINS)
            .assertIsDisplayed()
        rule.onNodeWithText(TEXT_COMMUTER).assertLabelEquals(LABEL_HIDE_COMMUTER_TRAINS)
            .assertIsDisplayed()

        // Change train categories to long-distance trains by hiding commuter trains
        rule.onNodeWithText(TEXT_COMMUTER).performClick()

        rule.onNodeWithText(TEXT_LONG_DISTANCE_TRAINS).assertIsDisplayed()
        rule.onNodeWithText(TEXT_COMMUTER_TRAINS).assertDoesNotExist()
        rule.onNodeWithText(TEXT_LONG_DISTANCE).assertLabelEquals(LABEL_HIDE_LONG_DISTANCE_TRAINS)
        rule.onNodeWithText(TEXT_COMMUTER).assertLabelEquals(LABEL_SHOW_COMMUTER_TRAINS)

        argumentCaptor<TimetableEvent>().apply {
            verify(onTimetableEvent, times(1)).invoke(capture())
            val event = firstValue as? TimetableEvent.SelectCategories
            assertThat(event).isNotNull()
            assertThat(event?.categories).containsExactly(Train.Category.LongDistance)
        }
    }

    @Test fun changeTimetableTypeFromArrivingToDeparting() {
        val timetable = listOf(
            Train(
                1, "ABC", Train.Category.LongDistance, timetable = listOf(
                    departure(1, "1", at("12:00")),
                    arrival(10, "2", at("12:10")),
                    departure(10, "2", at("12:15")),
                    arrival(18, "1", at("12:25"))
                )
            ),
            Train(
                2, "DEF", Train.Category.LongDistance, timetable = listOf(
                    departure(10, "1", at("12:45")),
                    arrival(1, "4", at("13:00"))
                )
            ),
            Train(
                3, "GHI", Train.Category.LongDistance, timetable = listOf(
                    departure(1, "5", at("12:30")),
                    arrival(10, "3", at("12:45"))
                )
            )
        )
        val viewState = TimetableViewState(
            station = pasila, timetable = timetable, stationNameMapper = testStationMapper,
            selectedTimetableTypes = setOf(TimetableRow.Type.Arrival)
        )
        val onTimetableEvent = mock<(TimetableEvent) -> Unit>()
        rule.setThemedContent {
            var state by remember(viewState) { mutableStateOf(viewState) }
            TimetableScreen(viewState = state, onEvent = { event: TimetableEvent ->
                if (event is TimetableEvent.SelectTimetableTypes)
                    state = state.copy(selectedTimetableTypes = event.types)
                onTimetableEvent(event)
            })
        }

        rule.onNodeWithText(TEXT_ARRIVING).assertDoesNotExist()
        rule.onNodeWithText(TEXT_DEPARTING).assertDoesNotExist()
        rule.onNodeWithText(TEXT_ARRIVING_TRAINS).assertIsDisplayed()
        rule.onNodeWithText(TEXT_DEPARTING_TRAINS).assertDoesNotExist()
        rule.onNodeWithSubstring("ABC, 1").assertExists()
        rule.onNodeWithSubstring("DEF, 2").assertDoesNotExist()
        rule.onNodeWithSubstring("GHI, 3").assertExists()
        rule.onNodeWithLabel(LABEL_HIDE_FILTERS).assertDoesNotExist()
        rule.onNodeWithLabel(LABEL_SHOW_FILTERS).assertIsDisplayed()

        // Show filters.
        rule.onNodeWithLabel(LABEL_SHOW_FILTERS).performClick()

        rule.onNodeWithLabel(LABEL_HIDE_FILTERS).assertIsDisplayed()
        rule.onNodeWithLabel(LABEL_SHOW_FILTERS).assertDoesNotExist()
        rule.onNodeWithText(TEXT_DEPARTING).assertLabelEquals(LABEL_SHOW_DEPARTING_TRAINS)
            .assertIsDisplayed()
        rule.onNodeWithText(TEXT_ARRIVING).assertLabelEquals(LABEL_HIDE_ARRIVING_TRAINS)
            .assertIsDisplayed()

        // Change timetable type type to departing trains by hiding arriving trains.
        rule.onNodeWithText(TEXT_ARRIVING).performClick()

        rule.onNodeWithText(TEXT_DEPARTING).assertLabelEquals(LABEL_HIDE_DEPARTING_TRAINS)
        rule.onNodeWithText(TEXT_ARRIVING).assertLabelEquals(LABEL_SHOW_ARRIVING_TRAINS)
        rule.onNodeWithText(TEXT_ARRIVING_TRAINS).assertDoesNotExist()
        rule.onNodeWithText(TEXT_DEPARTING_TRAINS).assertIsDisplayed()
        rule.onNodeWithSubstring("ABC, 1").assertExists()
        rule.onNodeWithSubstring("DEF, 2").assertExists()
        rule.onNodeWithSubstring("GHI, 3").assertDoesNotExist()

        argumentCaptor<TimetableEvent>().apply {
            verify(onTimetableEvent, times(1)).invoke(capture())
            val event = firstValue as? TimetableEvent.SelectTimetableTypes
            assertThat(event).isNotNull()
            assertThat(event?.types).containsExactly(TimetableRow.Type.Departure)
        }
    }
}
