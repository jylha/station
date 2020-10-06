package com.example.station.ui.train

import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.assertTextEquals
import androidx.ui.test.createComposeRule
import androidx.ui.test.onNodeWithLabel
import androidx.ui.test.onNodeWithText
import com.example.station.data.stations.StationNameMapper
import com.example.station.model.TimetableRow
import com.example.station.model.Train
import java.time.ZonedDateTime
import org.junit.Rule
import org.junit.Test


class TrainDetailsScreenTest {

    @get:Rule
    val rule = createComposeRule(disableTransitions = true)

    private val stationNames = mapOf(1 to "Helsinki", 30 to "Pasila")

    private val mapper = object : StationNameMapper {
        override fun stationName(stationUic: Int): String? = stationNames[stationUic]
    }

    @Test fun loadingTrainDetails() {
        val state = TrainDetailsViewState(isLoadingMapper = true)
        rule.setContent { TrainDetailsScreen(state) }

        rule.onNodeWithText("Retrieving train details.").assertIsDisplayed()
    }

    private val longDistanceTrain = Train(
        123, "ABC", Train.Category.LongDistance,
        timetable = listOf(
            TimetableRow.departure(
                "HKI", 1, "1", ZonedDateTime.parse("2020-01-01T10:00:00.0000Z")
            ),
            TimetableRow.arrival(
                "PSL", 30, "2", ZonedDateTime.parse("2020-01-01T10:10:00.000Z")
            )
        )
    )

    @Test fun longDistanceTrainDetails() {
        val state = TrainDetailsViewState(train = longDistanceTrain, nameMapper = mapper)
        rule.setContent { TrainDetailsScreen(state) }

        rule.onNodeWithLabel("Long-distance train ABC 123")
            .assertTextEquals("ABC 123").assertIsDisplayed()
        rule.onNodeWithLabel("From Helsinki")
            .assertTextEquals("Helsinki").assertIsDisplayed()
        rule.onNodeWithLabel("To Pasila")
            .assertTextEquals("Pasila").assertIsDisplayed()
    }

    private val intercityTrain = Train(
        10, "IC", Train.Category.LongDistance,
        timetable = listOf(
            TimetableRow.departure(
                "HKI", 1, "1", ZonedDateTime.parse("2020-01-01T10:00:00.0000Z")
            ),
            TimetableRow.arrival(
                "PSL", 30, "2", ZonedDateTime.parse("2020-01-01T10:10:00.000Z")
            )
        )
    )

    @Test fun intercityTrainDetails() {
        val state = TrainDetailsViewState(train = intercityTrain, nameMapper = mapper)
        rule.setContent { TrainDetailsScreen(state) }

        rule.onNodeWithLabel("Intercity train 10")
            .assertTextEquals("IC 10").assertIsDisplayed()
        rule.onNodeWithLabel("From Helsinki")
            .assertTextEquals("Helsinki").assertIsDisplayed()
        rule.onNodeWithLabel("To Pasila")
            .assertTextEquals("Pasila").assertIsDisplayed()
    }

    private val pendolinoTrain = Train(
        55, "S", Train.Category.LongDistance,
        timetable = listOf(
            TimetableRow.departure(
                "HKI", 1, "1", ZonedDateTime.parse("2020-01-01T10:00:00.0000Z")
            ),
            TimetableRow.arrival(
                "PSL", 30, "2", ZonedDateTime.parse("2020-01-01T10:10:00.000Z")
            )
        )
    )

    @Test fun pendolinoTrainDetails() {
        val state = TrainDetailsViewState(train = pendolinoTrain, nameMapper = mapper)
        rule.setContent { TrainDetailsScreen(state) }

        rule.onNodeWithLabel("Pendolino train 55")
            .assertTextEquals("S 55").assertIsDisplayed()
        rule.onNodeWithLabel("From Helsinki")
            .assertTextEquals("Helsinki").assertIsDisplayed()
        rule.onNodeWithLabel("To Pasila")
            .assertTextEquals("Pasila").assertIsDisplayed()
    }
}
