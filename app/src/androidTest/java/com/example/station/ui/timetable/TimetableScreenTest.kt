package com.example.station.ui.timetable

import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.createComposeRule
import androidx.ui.test.onNodeWithText
import com.example.station.model.Station
import com.example.station.model.Train
import org.junit.Rule
import org.junit.Test

class TimetableScreenTest {

    @get:Rule
    val rule = createComposeRule(disableTransitions = true)

    @Test fun loadingTimetable() {
        val state = TimetableViewState(isLoadingTimetable = true)
        rule.setContent { TimetableScreen(viewState = state) }

        rule.onNodeWithText("Retrieving timetable.").assertIsDisplayed()
    }

    @Test fun emptyTimetable() {
        val state = TimetableViewState(isLoadingTimetable = false, station = pasila)
        rule.setContent { TimetableScreen(viewState = state) }

        rule.onNodeWithText("Pasila").assertIsDisplayed()
        rule.onNodeWithText("All trains").assertIsDisplayed()
        rule.onNodeWithText("No trains are scheduled to stop at this station in the near future.")
            .assertIsDisplayed()
    }

    private val pasila = Station("Pasila", "PSL", 10, 24.933521, 60.198689)
    private val helsinki = Station("Helsinki", "HKI", 1, 24.941249, 60.172097)

    private val trains = listOf(
        Train(
            1, "IC", Train.Category.LongDistance, isRunning = true, timetable =
            listOf(

            )
        )
    )
}
