package com.example.station.ui.stations

import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.assertTextEquals
import androidx.ui.test.createComposeRule
import androidx.ui.test.onChildAt
import androidx.ui.test.onChildren
import androidx.ui.test.onNodeWithLabel
import androidx.ui.test.onNodeWithSubstring
import androidx.ui.test.onNodeWithText
import androidx.ui.test.onParent
import androidx.ui.test.performClick
import androidx.ui.test.performTextInput
import com.example.station.model.Station
import com.example.station.testutil.setThemedContent
import org.junit.Rule
import org.junit.Test

class StationsScreenTest {

    @get:Rule val rule = createComposeRule()

    @Test fun displayListOfStations() {
        val state = StationsViewState(
            stations = listOf(station("Helsinki", 1), station("Pasila", 2)),
        )
        rule.setThemedContent { StationsScreen(state = state, onSelect = {}) }

        rule.onNodeWithLabel("Nearest station").assertIsDisplayed()
        rule.onNodeWithLabel("Search station").assertIsDisplayed()

        rule.onNodeWithText("Select station").assertIsDisplayed()
        rule.onNodeWithText("RECENT", ignoreCase = true).assertDoesNotExist()
        rule.onNodeWithText("ALL STATIONS").assertIsDisplayed()
        rule.onNodeWithText("Helsinki").assertIsDisplayed()
        rule.onNodeWithText("Pasila").assertIsDisplayed()
    }

    @Test fun displayListOfRecentStations() {
        val state = StationsViewState(
            stations = listOf(station("Helsinki", 1), station("Pasila", 2)),
            recentStations = listOf(1)
        )
        rule.setThemedContent(darkMode = false) { StationsScreen(state = state, onSelect = {}) }

        rule.onNodeWithLabel("Nearest station").assertIsDisplayed()
        rule.onNodeWithLabel("Search station").assertIsDisplayed()

        rule.onNodeWithText("Select station").assertIsDisplayed()
        rule.onNodeWithText("RECENT").assertIsDisplayed()
            .onParent().onChildren()[0].assertTextEquals("RECENT")
            .onParent().onChildren()[1].assertTextEquals("Helsinki")
            .onParent().onChildren()[2].assertTextEquals("ALL STATIONS")
            .onParent().onChildren()[3].assertTextEquals("Helsinki")
            .onParent().onChildren()[4].assertTextEquals("Pasila")
    }

    @Test fun searchForStation() {
        val state = StationsViewState(
            stations = listOf(
                station("Helsinki", 1),
                station("Pasila", 2),
                station("Helsinki Airport", 3)
            )
        )
        rule.setThemedContent { StationsScreen(state = state, onSelect = {}) }

        rule.onNodeWithLabel("Nearest station").assertIsDisplayed()
        rule.onNodeWithLabel("Search station").assertIsDisplayed()
        rule.onNodeWithLabel("Search").assertDoesNotExist()

        rule.onNodeWithText("Select station")
        rule.onNodeWithText("ALL STATIONS").assertIsDisplayed()
            .onParent().onChildren()[1].assertTextEquals("Helsinki")
            .onParent().onChildren()[2].assertTextEquals("Pasila")
            .onParent().onChildren()[3].assertTextEquals("Helsinki Airport")
        rule.onNodeWithText("Search station").assertDoesNotExist()

        rule.onNodeWithLabel("Search station").performClick()

        rule.onNodeWithLabel("Nearest station").assertDoesNotExist()
        rule.onNodeWithLabel("Search station").assertDoesNotExist()
        rule.onNodeWithLabel("Search").assertIsDisplayed()

        rule.onNodeWithSubstring("Search station").assertIsDisplayed()
        rule.onNodeWithText("ALL STATIONS").assertIsDisplayed()
        rule.onNodeWithText("MATCHING STATIONS").assertDoesNotExist()

        rule.onNodeWithLabel("Search").onChildAt(1).performTextInput("h")

        rule.onNodeWithSubstring("Search station").assertDoesNotExist()
        rule.onNodeWithText("ALL STATIONS").assertDoesNotExist()
        rule.onNodeWithText("MATCHING STATIONS").assertIsDisplayed()
            .onParent().onChildren()[1].assertTextEquals("Helsinki")
            .onParent().onChildren()[2].assertTextEquals("Helsinki Airport")

        rule.onNodeWithLabel("Search").onChildAt(1).performTextInput("a")

        rule.onNodeWithText("ALL STATIONS").assertDoesNotExist()
        rule.onNodeWithText("MATCHING STATIONS").assertDoesNotExist()
        rule.onNodeWithText("No stations match the search.").assertIsDisplayed()
    }
}

private fun station(name: String, code: Int) = Station(
    passengerTraffic = true, type = Station.Type.Station, name = name, shortCode = "",
    code = code, countryCode = "FI", 10.0, 20.0
)
