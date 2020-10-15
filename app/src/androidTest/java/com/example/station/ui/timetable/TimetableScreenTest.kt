package com.example.station.ui.timetable

import androidx.compose.runtime.Composable
import androidx.ui.test.assert
import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.assertLabelEquals
import androidx.ui.test.createComposeRule
import androidx.ui.test.hasSubstring
import androidx.ui.test.onNodeWithSubstring
import androidx.ui.test.onNodeWithText
import com.example.station.data.stations.LocalizedStationNames
import com.example.station.model.Station
import com.example.station.model.Train
import com.example.station.model.arrival
import com.example.station.model.departure
import com.example.station.ui.components.StationNameProvider
import com.example.station.ui.theme.StationTheme
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import org.junit.Rule
import org.junit.Test

class TimetableScreenTest {

    @get:Rule
    val rule = createComposeRule(disableTransitions = true)

    private val helsinki = Station("Helsinki", "HKI", 1, 24.941249, 60.172097)
    private val pasila = Station("Pasila", "PSL", 10, 24.933521, 60.198689)
    private val tikkurila = Station("Tikkurila", "TKL", 18, 25.291811, 64.771091)
    private val testStations = listOf(helsinki, pasila, tikkurila)
    private val testStationMapper = LocalizedStationNames.from(testStations)

    private fun setThemedContent(darkMode: Boolean = true, content: @Composable () -> Unit) {
        rule.setContent {
            StationTheme(darkMode) {
                StationNameProvider(nameMapper = testStationMapper) {
                    content()
                }
            }
        }
    }

    private fun at(time: String, date: String = "2020-01-01"): ZonedDateTime {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .withZone(ZoneId.systemDefault())
        return ZonedDateTime.parse("$date $time", formatter)
    }

    @Test fun loadingTimetable() {
        val state = TimetableViewState(isLoadingTimetable = true)
        setThemedContent { TimetableScreen(viewState = state) }

        rule.onNodeWithText("Retrieving timetable.").assertIsDisplayed()
    }

    @Test fun emptyTimetable() {
        val state = TimetableViewState(station = pasila, stationNameMapper = testStationMapper)
        setThemedContent { TimetableScreen(viewState = state) }

        rule.onNodeWithText("Pasila").assertIsDisplayed()
        rule.onNodeWithText("All trains").assertIsDisplayed()
        rule.onNodeWithText("No trains are scheduled to stop at this station in the near future.")
            .assertIsDisplayed()
    }

    private val trains = listOf(
        Train(
            1, "IC", Train.Category.LongDistance, isRunning = true, timetable =
            listOf(
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
            2, "S", Train.Category.LongDistance, isRunning = true, timetable =
            listOf(
                departure(10, "1", at("12:45")),
                arrival(1, "4", at("13:00"))
            )
        ),
        Train(
            3, "ABC", Train.Category.Commuter, commuterLineId = "Z", isRunning = true,
            timetable = listOf(
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
            station = pasila, timetable = trains,
            stationNameMapper = testStationMapper
        )
        setThemedContent { TimetableScreen(viewState = state) }

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
}
