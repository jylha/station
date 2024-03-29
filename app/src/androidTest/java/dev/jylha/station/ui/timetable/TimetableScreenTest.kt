package dev.jylha.station.ui.timetable

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.common.truth.Truth.assertThat
import dev.jylha.station.data.stations.LocalizedStationNames
import dev.jylha.station.model.Station
import dev.jylha.station.model.TimetableRow
import dev.jylha.station.model.Train
import dev.jylha.station.model.arrival
import dev.jylha.station.model.departure
import dev.jylha.station.testutil.at
import dev.jylha.station.testutil.hasSubstring
import dev.jylha.station.testutil.onNodeWithSubstring
import dev.jylha.station.testutil.setThemedContent
import kotlinx.collections.immutable.persistentListOf
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

class TimetableScreenTest {

    @get:Rule val rule = createComposeRule()

    private val helsinki = Station("Helsinki", "HKI", 1, 24.941249, 60.172097)
    private val pasila = Station("Pasila", "PSL", 10, 24.933521, 60.198689)
    private val tikkurila = Station("Tikkurila", "TKL", 18, 25.291811, 64.771091)
    private val testStations = listOf(helsinki, pasila, tikkurila)
    private val testStationMapper = LocalizedStationNames.from(testStations)

    @Test fun loadingTimetable() {
        val state = TimetableViewState(isLoadingTimetable = true)
        rule.mainClock.autoAdvance = false
        rule.setThemedContent { TimetableScreen(state = state, stationCode = pasila.code) }
        rule.mainClock.advanceTimeBy(100)

        rule.onNodeWithText("Retrieving timetable.").assertIsDisplayed()
    }

    @Test fun emptyTimetable() {
        val state = TimetableViewState(station = pasila, stationNameMapper = testStationMapper)
        rule.setThemedContent { TimetableScreen(state = state, stationCode = pasila.code) }

        rule.onNodeWithText("Pasila").assertIsDisplayed()
        rule.onNodeWithText(TEXT_ALL_TRAINS).assertIsDisplayed()
        rule.onNodeWithText("No trains are scheduled to stop at this station in the near future.")
            .assertIsDisplayed()
    }

    private val trains = persistentListOf(
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
        ),
        Train(
            4, "DEF", Train.Category.Commuter, timetable = listOf(
                departure(10, "4", at("13:00")),
                arrival(1, "4", at("13:30"))
            )
        ),
        Train(
            5, "IC", Train.Category.LongDistance, timetable = listOf(
                departure(1, "1", at("13:30")),
                arrival(10, "5", at("14:15"))
            )
        )
    )

    @Test fun timetable() {
        val state = TimetableViewState(
            station = pasila, timetable = trains, stationNameMapper = testStationMapper
        )
        rule.setThemedContent { TimetableScreen(state = state, stationCode = pasila.code) }

        rule.onNodeWithText(TEXT_ALL_TRAINS).assertIsDisplayed()
        rule.onNodeWithContentDescription(LABEL_SELECT_STATION).assertIsDisplayed()
        rule.onNodeWithContentDescription(LABEL_SHOW_FILTERS).assertIsDisplayed()
        rule.onNodeWithContentDescription(LABEL_HIDE_FILTERS).assertDoesNotExist()

        rule.onNodeWithContentDescription("Intercity train 1").assertIsDisplayed()
            .assert(hasSubstring("Helsinki"))
            .assert(hasSubstring("Tikkurila"))
            .assert(hasSubstring("ARRIVED, 12:12, +2"))
            .assert(hasSubstring("TRACK, 2"))
            .assert(hasSubstring("DEPARTS, 12:15"))
            .assertContentDescriptionEquals(
                "Intercity train 1",
                "from Helsinki",
                "to Tikkurila",
                "arrived to track 2 at 12:12",
                "departs at 12:15",
            )

        rule.onNodeWithContentDescription("Pendolino train 2").assertIsDisplayed()
            .assert(hasSubstring("S, 2"))
            .assert(hasSubstring("Pasila"))
            .assert(hasSubstring("Helsinki"))
            .assert(hasSubstring("TRACK, 1"))
            .assert(hasSubstring("DEPARTS, 12:45"))
            .assertContentDescriptionEquals(
                "Pendolino train 2",
                "from Pasila",
                "to Helsinki",
                "departs from track 1 at 12:45"
            )

        rule.onNodeWithContentDescription("Z commuter train").assertIsDisplayed()
            .assert(hasSubstring("Helsinki"))
            .assert(hasSubstring("Pasila"))
            .assert(hasSubstring("ARRIVES, 12:45, 12:48"))
            .assert(hasSubstring("Z"))
            .assertContentDescriptionEquals(
                "Z commuter train",
                "from Helsinki",
                "to Pasila",
                "estimated time of arrival to track 3 at 12:48"
            )

        rule.onNodeWithContentDescription("Commuter train D E F 4").assertIsDisplayed()
            .assert(hasSubstring("DEF, 4"))
            .assert(hasSubstring("Pasila"))
            .assert(hasSubstring("Helsinki"))
            .assert(hasSubstring("TRACK, 4"))
            .assert(hasSubstring("DEPARTS, 13:00"))
            .assertContentDescriptionEquals(
                "Commuter train D E F 4",
                "from Pasila",
                "to Helsinki",
                "departs from track 4 at 13:00",
            )

        rule.onNodeWithContentDescription("Intercity train 5").assertIsDisplayed()
            .assert(hasSubstring("Helsinki"))
            .assert(hasSubstring("Pasila"))
            .assert(hasSubstring("ARRIVES, 14:15"))
            .assert(hasSubstring("TRACK, 5"))
            .assertContentDescriptionEquals(
                "Intercity train 5",
                "from Helsinki",
                "to Pasila",
                "arrives to track 5 at 14:15",
            )
    }

    @Test fun commuterTrainCategorySelected_clickCommuterTrainCategory() {
        val receivedEvents = mutableListOf<TimetableEvent>()
        val state = TimetableViewState(
            station = helsinki, timetable = trains, stationNameMapper = testStationMapper,
            selectedTrainCategories = TrainCategories(Train.Category.Commuter)
        )

        rule.apply {
            setThemedContent {
                TimetableScreen(state = state, stationCode = helsinki.code,
                    onEvent = { event -> receivedEvents += event }
                )
            }

            onNodeWithText(TEXT_ALL_TRAINS).assertDoesNotExist()
            onNodeWithText(TEXT_COMMUTER_TRAINS).assertIsDisplayed()
            onNodeWithText(TEXT_LONG_DISTANCE_TRAINS).assertDoesNotExist()
            onNodeWithContentDescription(LABEL_HIDE_FILTERS).assertDoesNotExist()
            onNodeWithContentDescription(LABEL_SHOW_FILTERS).assertIsDisplayed()

            // Show filters
            onNodeWithContentDescription(LABEL_SHOW_FILTERS).performClick()
            mainClock.advanceTimeBy(100)

            onNodeWithContentDescription(LABEL_SHOW_FILTERS).assertDoesNotExist()
            onNodeWithContentDescription(LABEL_HIDE_FILTERS).assertIsDisplayed()

            onNodeWithText(TEXT_LONG_DISTANCE)
                .assertContentDescriptionEquals(LABEL_SHOW_LONG_DISTANCE_TRAINS)
                .assertIsDisplayed()
            onNodeWithText(TEXT_COMMUTER)
                .assertContentDescriptionEquals(LABEL_HIDE_COMMUTER_TRAINS)
                .assertIsDisplayed()

            onNodeWithText(TEXT_COMMUTER).performClick()
        }

        assertThat(receivedEvents).containsExactly(
            TimetableEvent.SelectCategories(TrainCategories(Train.Category.LongDistance))
        )
    }

    @Test fun longDistanceTrainCategorySelected_clickCommuterTrainCategory() {
        val receivedEvents = mutableListOf<TimetableEvent>()
        val state = TimetableViewState(
            station = helsinki, timetable = trains, stationNameMapper = testStationMapper,
            selectedTrainCategories = TrainCategories(Train.Category.LongDistance)
        )

        rule.apply {
            setThemedContent {
                TimetableScreen(state = state, stationCode = helsinki.code,
                    onEvent = { event -> receivedEvents += event }
                )
            }

            onNodeWithText(TEXT_ALL_TRAINS).assertDoesNotExist()
            onNodeWithText(TEXT_COMMUTER_TRAINS).assertDoesNotExist()
            onNodeWithText(TEXT_LONG_DISTANCE_TRAINS).assertIsDisplayed()
            onNodeWithContentDescription(LABEL_HIDE_FILTERS).assertDoesNotExist()
            onNodeWithContentDescription(LABEL_SHOW_FILTERS).assertIsDisplayed()

            // Show filters
            onNodeWithContentDescription(LABEL_SHOW_FILTERS).performClick()

            onNodeWithContentDescription(LABEL_SHOW_FILTERS).assertDoesNotExist()
            onNodeWithContentDescription(LABEL_HIDE_FILTERS).assertIsDisplayed()

            onNodeWithText(TEXT_LONG_DISTANCE)
                .assertContentDescriptionEquals(LABEL_HIDE_LONG_DISTANCE_TRAINS)
                .assertIsDisplayed()
            onNodeWithText(TEXT_COMMUTER)
                .assertContentDescriptionEquals(LABEL_SHOW_COMMUTER_TRAINS)
                .assertIsDisplayed()

            onNodeWithText(TEXT_COMMUTER).performClick()
        }

        assertThat(receivedEvents).containsExactly(
            TimetableEvent.SelectCategories(
                TrainCategories(Train.Category.Commuter, Train.Category.LongDistance)
            )
        )

    }

    @Test fun arrivingTrainCategorySelected_clickArrivingTrainCategory() {
        val timetable = persistentListOf(
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
        val receivedEvents = mutableListOf<TimetableEvent>()
        val viewState = TimetableViewState(
            station = pasila, timetable = timetable, stationNameMapper = testStationMapper,
            selectedTimetableTypes = TimetableTypes(TimetableRow.Type.Arrival)
        )

        rule.apply {
            setThemedContent {
                TimetableScreen(state = viewState, stationCode = pasila.code,
                    onEvent = { event -> receivedEvents += event }
                )
            }

            onNodeWithText(TEXT_ARRIVING).assertDoesNotExist()
            onNodeWithText(TEXT_DEPARTING).assertDoesNotExist()
            onNodeWithText(TEXT_ARRIVING_TRAINS).assertIsDisplayed()
            onNodeWithText(TEXT_DEPARTING_TRAINS).assertDoesNotExist()
            onNodeWithSubstring("ABC, 1").assertExists()
            onNodeWithSubstring("DEF, 2").assertDoesNotExist()
            onNodeWithSubstring("GHI, 3").assertExists()
            onNodeWithContentDescription(LABEL_HIDE_FILTERS).assertDoesNotExist()
            onNodeWithContentDescription(LABEL_SHOW_FILTERS).assertIsDisplayed()

            // Show filters.
            onNodeWithContentDescription(LABEL_SHOW_FILTERS).performClick()
            mainClock.advanceTimeBy(100)

            onNodeWithContentDescription(LABEL_HIDE_FILTERS).assertIsDisplayed()
            onNodeWithContentDescription(LABEL_SHOW_FILTERS).assertDoesNotExist()
            onNodeWithText(TEXT_DEPARTING)
                .assertContentDescriptionEquals(LABEL_SHOW_DEPARTING_TRAINS)
                .assertIsDisplayed()
            onNodeWithText(TEXT_ARRIVING)
                .assertContentDescriptionEquals(LABEL_HIDE_ARRIVING_TRAINS)
                .assertIsDisplayed()

            // Change timetable type type to departing trains by hiding arriving trains.
            onNodeWithText(TEXT_ARRIVING).performClick()
        }

        assertThat(receivedEvents).containsExactly(
            TimetableEvent.SelectTimetableTypes(TimetableTypes(TimetableRow.Type.Departure))
        )
    }
}
