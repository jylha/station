package dev.jylha.station.ui.train

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertLabelEquals
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasSubstring
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithSubstring
import androidx.compose.ui.test.onNodeWithText
import dev.jylha.station.data.stations.StationNameMapper
import dev.jylha.station.model.Train
import dev.jylha.station.model.arrival
import dev.jylha.station.model.departure
import dev.jylha.station.testutil.at
import dev.jylha.station.testutil.setThemedContent
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class TrainDetailsScreenTest {

    @get:Rule val rule = createComposeRule()

    private val stationNameMapper = object : StationNameMapper {
        override fun stationName(stationCode: Int): String? = stationNames[stationCode]
        private val stationNames = mapOf(
            1 to "Helsinki",
            18 to "Tikkurila",
            30 to "Pasila",
            160 to "Tampere",
            280 to "Seinäjoki"
        )
    }

    @Test fun loadingTrainDetails() {
        val state = TrainDetailsViewState(isLoadingMapper = true)
        rule.mainClock.autoAdvance = false
        rule.setThemedContent(darkMode = false) { TrainDetailsScreen(state) }
        rule.mainClock.advanceTimeBy(100)

        rule.onNodeWithText("Retrieving train details.").assertIsDisplayed()
    }

    @Test fun longDistanceTrainDetails() {
        val longDistanceTrain = Train(
            123, "ABC", Train.Category.LongDistance,
            timetable = listOf(
                departure(1, "1", at("10:00")),
                arrival(30, "2", at("10:10"))
            )
        )
        val state = TrainDetailsViewState(train = longDistanceTrain, nameMapper = stationNameMapper)
        rule.setThemedContent { TrainDetailsScreen(state) }

        rule.onNodeWithContentDescription("Long-distance train ABC 123")
            .assertTextEquals("ABC 123").assertIsDisplayed()
        rule.onNodeWithContentDescription("from Helsinki", useUnmergedTree = true)
            .assertTextEquals("Helsinki").assertIsDisplayed()
        rule.onNodeWithContentDescription("to Pasila", useUnmergedTree = true)
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
        rule.setThemedContent { TrainDetailsScreen(state) }

        rule.onNodeWithContentDescription("Intercity train 10")
            .assertTextEquals("IC 10").assertIsDisplayed()
        rule.onNodeWithContentDescription("from Helsinki", useUnmergedTree = true)
            .assertTextEquals("Helsinki").assertIsDisplayed()
        rule.onNodeWithContentDescription("to Tampere", useUnmergedTree = true)
            .assertTextEquals("Tampere").assertIsDisplayed()

        rule.onNodeWithText("Helsinki, 12:24").assertIsDisplayed()
            .assertLabelEquals("Helsinki, departed at 12:24")

        rule.onNode(hasSubstring("Pasila"))
            .assertTextEquals("Pasila, 12:29, +1, 12:32, +2")
            .assertLabelEquals("Pasila, arrived at 12:29, departed at 12:32")
            .assertIsDisplayed()

        rule.onNode(hasSubstring("Tikkurila"))
            .assertTextEquals("Tikkurila, 12:38, -1, 12:41")
            .assertLabelEquals("Tikkurila, arrived at 12:38, departed at 12:41")
            .assertIsDisplayed()

        rule.onNode(hasSubstring("Tampere") and hasSubstring("13:"))
            .assertTextEquals("Tampere, 13:58, 13:56")
            .assertLabelEquals("Tampere, estimated time of arrival at 13:56")
            .assertIsDisplayed()
    }

    @Test fun pendolinoTrainDetails() {
        val pendolinoTrain = Train(
            55, "S", Train.Category.LongDistance,
            timetable = listOf(
                departure(1, "1", at("10:00")),
                arrival(30, "2", at("10:10"))
            )
        )
        val state = TrainDetailsViewState(train = pendolinoTrain, nameMapper = stationNameMapper)
        rule.setThemedContent(darkMode = false) { TrainDetailsScreen(state) }

        rule.onNodeWithContentDescription("Pendolino train 55")
            .assertTextEquals("S 55").assertIsDisplayed()
        rule.onNodeWithContentDescription("from Helsinki", useUnmergedTree = true)
            .assertTextEquals("Helsinki").assertIsDisplayed()
        rule.onNodeWithContentDescription("to Pasila", useUnmergedTree = true)
            .assertTextEquals("Pasila").assertIsDisplayed()
    }

    @Test fun commuterTrainDetails() {
        val commuterTrain = Train(
            123, "ABC", Train.Category.Commuter,
            timetable = listOf(
                departure(1, "1", at("10:00")),
                arrival(30, "2", at("10:10")),
                departure(30, "2", at("10:11")),
                arrival(18, "1", at("10:20"))
            )
        )
        val state = TrainDetailsViewState(train = commuterTrain, nameMapper = stationNameMapper)
        rule.setThemedContent { TrainDetailsScreen(state) }

        rule.onNodeWithContentDescription("Commuter train ABC 123")
            .assertTextEquals("ABC 123").assertIsDisplayed()
        rule.onNodeWithContentDescription("from Helsinki", useUnmergedTree = true)
            .assertTextEquals("Helsinki").assertIsDisplayed()
        rule.onNodeWithContentDescription("to Tikkurila", useUnmergedTree = true)
            .assertTextEquals("Tikkurila").assertIsDisplayed()
    }

    @Test fun cancelledTrainDetails() {
        val commuterTrain = Train(
            456, "DEF", Train.Category.Commuter, "D",
            timetable = listOf(
                departure(1, "5", at("11:00")),
                arrival(30, "2", at("11:15"), cancelled = true),
                departure(30, "2", at("11:16"), cancelled = true),
                arrival(160, "1", at("12:00"))
            )
        )
        val state = TrainDetailsViewState(train = commuterTrain, nameMapper = stationNameMapper)
        rule.setThemedContent { TrainDetailsScreen(state) }

        rule.onNodeWithContentDescription("D commuter train").assertIsDisplayed()
        rule.onNodeWithContentDescription("from Helsinki", useUnmergedTree = true)
            .assertTextEquals("Helsinki").assertIsDisplayed()
        rule.onNodeWithContentDescription("to Tampere", useUnmergedTree = true)
            .assertTextEquals("Tampere").assertIsDisplayed()
        rule.onNodeWithSubstring("Pasila")
            .assertTextEquals("Pasila, CANCELLED, CANCELLED")
            .assertLabelEquals("Pasila, arrival is cancelled, departure is cancelled")
            .assertIsDisplayed()
    }
}
