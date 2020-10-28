package com.example.station.ui.train

import androidx.compose.runtime.Composable
import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.assertLabelEquals
import androidx.ui.test.assertTextEquals
import androidx.ui.test.createComposeRule
import androidx.ui.test.hasSubstring
import androidx.ui.test.onNodeWithLabel
import androidx.ui.test.onNodeWithText
import com.example.station.data.stations.StationNameMapper
import com.example.station.model.Train
import com.example.station.model.arrival
import com.example.station.model.departure
import com.example.station.ui.theme.StationTheme
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import org.junit.Rule
import org.junit.Test


class TrainDetailsScreenTest {

    @get:Rule val rule = createComposeRule()

    private val stationNameMapper = object : StationNameMapper {
        override fun stationName(stationUic: Int): String? = stationNames[stationUic]
        private val stationNames = mapOf(
            1 to "Helsinki",
            18 to "Tikkurila",
            30 to "Pasila",
            160 to "Tampere",
            280 to "Seinäjoki"
        )
    }

    private fun setThemedContent(darkTheme: Boolean = true, content: @Composable () -> Unit) {
        rule.setContent {
            StationTheme(darkTheme) {
                content()
            }
        }
    }

    private fun at(time: String, date: String = "2020-01-01"): ZonedDateTime {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .withZone(ZoneId.systemDefault())
        return ZonedDateTime.parse("$date $time", formatter)
    }

    @Test fun loadingTrainDetails() {
        val state = TrainDetailsViewState(isLoadingMapper = true)
        rule.clockTestRule.pauseClock()
        rule.setContent { TrainDetailsScreen(state) }
        rule.clockTestRule.advanceClock(100)

        rule.onNodeWithText("Retrieving train details.").assertIsDisplayed()
    }

    private val longDistanceTrain = Train(
        123, "ABC", Train.Category.LongDistance,
        timetable = listOf(
            departure(1, "1", ZonedDateTime.parse("2020-01-01T10:00Z")),
            arrival(30, "2", ZonedDateTime.parse("2020-01-01T10:10Z"))
        )
    )

    @Test fun longDistanceTrainDetails() {
        val state = TrainDetailsViewState(train = longDistanceTrain, nameMapper = stationNameMapper)
        rule.setContent { TrainDetailsScreen(state) }

        rule.onNodeWithLabel("Long-distance train ABC 123")
            .assertTextEquals("ABC 123").assertIsDisplayed()
        rule.onNodeWithLabel("From Helsinki", useUnmergedTree = true)
            .assertTextEquals("Helsinki").assertIsDisplayed()
        rule.onNodeWithLabel("To Pasila", useUnmergedTree = true)
            .assertTextEquals("Pasila").assertIsDisplayed()
    }

    private val intercityTrain = Train(
        10, "IC", Train.Category.LongDistance,
        timetable = listOf(
            departure(
                1, "1", at("12:24"),
                actualTime = at("12:24"), differenceInMinutes = 0
            ),
            arrival(
                30, "2", at("12:28"),
                actualTime = at("12:29"), differenceInMinutes = 1
            ),
            departure(
                30, "2", at("12:30"),
                actualTime = at("12:32"), differenceInMinutes = 2
            ),
            arrival(
                18, "2", at("12:39"),
                actualTime = at("12:38"), differenceInMinutes = -1
            ),
            departure(
                18, "2", at("12:41"),
                actualTime = at("12:41"), differenceInMinutes = 0
            ),
            arrival(
                160, "1", at("13:58"),
                estimatedTime = at("13:56"), differenceInMinutes = -2
            ),
        )
    )

    @Test fun intercityTrainDetails() {
        val state = TrainDetailsViewState(train = intercityTrain, nameMapper = stationNameMapper)
        setThemedContent { TrainDetailsScreen(state) }

        rule.onNodeWithLabel("Intercity train 10")
            .assertTextEquals("IC 10").assertIsDisplayed()
        rule.onNodeWithLabel("From Helsinki", useUnmergedTree = true)
            .assertTextEquals("Helsinki").assertIsDisplayed()
        rule.onNodeWithLabel("To Tampere", useUnmergedTree = true)
            .assertTextEquals("Tampere").assertIsDisplayed()

        rule.onNodeWithText("Helsinki, 12:24").assertIsDisplayed()
            .assertLabelEquals("Helsinki, Departed at 12:24")

        rule.onNode(hasSubstring("Pasila"))
            .assertTextEquals("Pasila, 12:29, +1, 12:32, +2")
            .assertLabelEquals("Pasila, Arrived at 12:29, Departed at 12:32")
            .assertIsDisplayed()

        rule.onNode(hasSubstring("Tikkurila"))
            .assertTextEquals("Tikkurila, 12:38, -1, 12:41")
            .assertLabelEquals("Tikkurila, Arrived at 12:38, Departed at 12:41")
            .assertIsDisplayed()

        rule.onNode(hasSubstring("Tampere") and hasSubstring("13:"))
            .assertTextEquals("Tampere, 13:58, 13:56")
            .assertLabelEquals("Tampere, Estimated time of arrival 13:56")
            .assertIsDisplayed()
    }

    private val pendolinoTrain = Train(
        55, "S", Train.Category.LongDistance,
        timetable = listOf(
            departure(1, "1", ZonedDateTime.parse("2020-01-01T10:00:00.0000Z")),
            arrival(30, "2", ZonedDateTime.parse("2020-01-01T10:10:00.000Z"))
        )
    )

    @Test fun pendolinoTrainDetails() {
        val state = TrainDetailsViewState(train = pendolinoTrain, nameMapper = stationNameMapper)
        setThemedContent(darkTheme = false) { TrainDetailsScreen(state) }

        rule.onNodeWithLabel("Pendolino train 55")
            .assertTextEquals("S 55").assertIsDisplayed()
        rule.onNodeWithLabel("From Helsinki", useUnmergedTree = true)
            .assertTextEquals("Helsinki").assertIsDisplayed()
        rule.onNodeWithLabel("To Pasila", useUnmergedTree = true)
            .assertTextEquals("Pasila").assertIsDisplayed()
    }

    private val commuterTrain = Train(
        123, "ABC", Train.Category.Commuter,
        timetable = listOf(
            departure(1, "1", ZonedDateTime.parse("2020-01-01T10:00:00.0000Z")),
            arrival(30, "2", ZonedDateTime.parse("2020-01-01T10:10:00.000Z")),
            departure(30, "2", ZonedDateTime.parse("2020-01-01T10:11:00.000Z")),
            arrival(18, "1", ZonedDateTime.parse("2020-01-01T10:20:00.000Z"))
        )
    )

    @Test fun commuterTrainDetails() {
        val state = TrainDetailsViewState(train = commuterTrain, nameMapper = stationNameMapper)
        rule.setContent { TrainDetailsScreen(state) }

        rule.onNodeWithLabel("Commuter train ABC 123")
            .assertTextEquals("ABC 123").assertIsDisplayed()
        rule.onNodeWithLabel("From Helsinki", useUnmergedTree = true)
            .assertTextEquals("Helsinki").assertIsDisplayed()
        rule.onNodeWithLabel("To Tikkurila", useUnmergedTree = true)
            .assertTextEquals("Tikkurila").assertIsDisplayed()
    }

}
