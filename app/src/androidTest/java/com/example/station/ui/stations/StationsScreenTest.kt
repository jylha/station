package com.example.station.ui.stations

import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.assertTextEquals
import androidx.ui.test.createComposeRule
import androidx.ui.test.onChildren
import androidx.ui.test.onNodeWithText
import androidx.ui.test.onParent
import com.example.station.model.Station
import org.junit.Rule
import org.junit.Test

class StationsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule(disableTransitions = true)


    @Test fun displayListOfStations() {
        val state = StationsViewState(
            stations = listOf(station("Helsinki", 1), station("Pasila", 2)),
        )
        composeTestRule.setContent { StationsScreen(state = state, onSelect = {}) }

        composeTestRule.onNodeWithText("RECENT", ignoreCase = true).assertDoesNotExist()
        composeTestRule.onNodeWithText("ALL STATIONS").assertIsDisplayed()
        composeTestRule.onNodeWithText("Helsinki").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pasila").assertIsDisplayed()
    }

    @Test fun displayListOfRecentStations() {
        val state = StationsViewState(
            stations = listOf(station("Helsinki", 1), station("Pasila", 2)),
            recentStations = listOf(1)
        )
        composeTestRule.setContent { StationsScreen(state = state, onSelect = {}) }

        composeTestRule.onNodeWithText("RECENT").assertIsDisplayed()
            .onParent().onChildren()[0].assertTextEquals("RECENT")
            .onParent().onChildren()[1].assertTextEquals("Helsinki")
            .onParent().onChildren()[2].assertTextEquals("ALL STATIONS")
            .onParent().onChildren()[3].assertTextEquals("Helsinki")
            .onParent().onChildren()[4].assertTextEquals("Pasila")
    }
}

private fun station(name: String, uic: Int) = Station(
    passengerTraffic = true, type = Station.Type.Station, name = name, shortCode = "",
    uic = uic, countryCode = "FI", 10.0, 20.0
)
