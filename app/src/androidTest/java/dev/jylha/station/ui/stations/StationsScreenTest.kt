package dev.jylha.station.ui.stations

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import dev.jylha.station.model.Station
import dev.jylha.station.testutil.onNodeWithSubstring
import dev.jylha.station.testutil.setThemedContent
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

private const val LABEL_NEAREST_STATION = "Nearest station"
private const val LABEL_SEARCH_STATION = "Search station"
private const val LABEL_SEARCH = "Search"
private const val LABEL_CLOSE_SEARCH = "Close search"

private const val TEXT_RECENT = "RECENT"
private const val TEXT_ALL_STATIONS = "ALL STATIONS"
private const val TEXT_MATCHING_STATIONS = "MATCHING STATIONS"
private const val TEXT_SELECT_STATION = "Select station"

@Ignore("Sticky header hack breaks these tests")
class StationsScreenTest {

    @get:Rule val rule = createComposeRule()

    @Test fun displayListOfStations() {
        val state = StationsViewState(
            stations = listOf(
                station("Helsinki", 1),
                station("Pasila", 2)
            ),
        )
        rule.setThemedContent {
            StationsScreen(viewState = state, onSelect = {}, onSelectNearest = {})
        }

        rule.onNodeWithContentDescription(LABEL_NEAREST_STATION).assertIsDisplayed()
        rule.onNodeWithContentDescription(LABEL_SEARCH_STATION).assertIsDisplayed()

        rule.onNodeWithText(TEXT_SELECT_STATION).assertIsDisplayed()
        rule.onNodeWithText(TEXT_RECENT, ignoreCase = true).assertDoesNotExist()
        rule.onNodeWithText(TEXT_ALL_STATIONS).assertIsDisplayed()
        rule.onNodeWithText("Helsinki").assertIsDisplayed()
        rule.onNodeWithText("Pasila").assertIsDisplayed()
    }

    @Test fun displayListOfRecentStations() {
        val state = StationsViewState(
            stations = listOf(
                station("Helsinki", 1),
                station("Pasila", 2)
            ),
            recentStations = listOf(1)
        )
        rule.setThemedContent(darkMode = false) {
            StationsScreen(viewState = state, onSelect = {}, onSelectNearest = {})
        }

        rule.onNodeWithContentDescription(LABEL_NEAREST_STATION).assertIsDisplayed()
        rule.onNodeWithContentDescription(LABEL_SEARCH_STATION).assertIsDisplayed()

        rule.onNodeWithText(TEXT_SELECT_STATION).assertIsDisplayed()
        rule.onNodeWithText(TEXT_RECENT).assertIsDisplayed()
            .onParent().onChildren()[0].assertTextEquals(TEXT_RECENT)
            .onParent().onChildren()[1].assertTextEquals("Helsinki")
            .onParent().onChildren()[2].assertTextEquals(TEXT_ALL_STATIONS)
            .onParent().onChildren()[3].assertTextEquals("H")
            .onParent().onChildren()[4].assertTextEquals("Helsinki")
            .onParent().onChildren()[5].assertTextEquals("P")
            .onParent().onChildren()[6].assertTextEquals("Pasila")
    }

    @Test fun searchForStation() {
        val state = StationsViewState(
            stations = listOf(
                station("Helsinki", 1),
                station("Pasila", 2),
                station("Helsinki Airport", 3)
            )
        )
        rule.setThemedContent {
            StationsScreen(viewState = state, onSelect = {}, onSelectNearest = {})
        }

        rule.onNodeWithContentDescription(LABEL_NEAREST_STATION).assertIsDisplayed()
        rule.onNodeWithContentDescription(LABEL_SEARCH_STATION).assertIsDisplayed()
        rule.onNodeWithContentDescription(LABEL_SEARCH).assertDoesNotExist()
        rule.onNodeWithContentDescription(LABEL_CLOSE_SEARCH).assertDoesNotExist()

        rule.onNodeWithText(TEXT_SELECT_STATION).assertIsDisplayed()
        rule.onNodeWithText(TEXT_ALL_STATIONS).assertIsDisplayed()
            .onParent().onChildren()[1].assertTextEquals("H")
            .onParent().onChildren()[2].assertTextEquals("Helsinki")
            .onParent().onChildren()[3].assertTextEquals("Helsinki Airport")
            .onParent().onChildren()[4].assertTextEquals("P")
            .onParent().onChildren()[5].assertTextEquals("Pasila")


        rule.onNodeWithContentDescription(LABEL_SEARCH_STATION).performClick()

        rule.onNodeWithContentDescription(LABEL_NEAREST_STATION).assertDoesNotExist()
        rule.onNodeWithContentDescription(LABEL_SEARCH_STATION).assertDoesNotExist()
        rule.onNodeWithContentDescription(LABEL_SEARCH).assertIsDisplayed()
        rule.onNodeWithContentDescription(LABEL_CLOSE_SEARCH).assertIsDisplayed()

        rule.onNodeWithSubstring("Search station").assertIsDisplayed()
        rule.onNodeWithText(TEXT_ALL_STATIONS).assertIsDisplayed()
        rule.onNodeWithText(TEXT_MATCHING_STATIONS).assertDoesNotExist()

        rule.onNodeWithContentDescription(LABEL_SEARCH).onChildAt(1).performTextInput("h")
        rule.onNodeWithContentDescription(LABEL_SEARCH).onChildAt(1).assert(hasText("h"))

        rule.onNodeWithSubstring("Search station").assertDoesNotExist()
        rule.onNodeWithText(TEXT_ALL_STATIONS).assertDoesNotExist()
        rule.onNodeWithText(TEXT_MATCHING_STATIONS).assertIsDisplayed()
            .onParent().onChildren()[1].assertTextEquals("H")
            .onParent().onChildren()[2].assertTextEquals("Helsinki")
            .onParent().onChildren()[3].assertTextEquals("Helsinki Airport")

        rule.onNodeWithContentDescription(LABEL_SEARCH).onChildAt(1).performTextInput("ha")
        rule.onNodeWithContentDescription(LABEL_SEARCH).onChildAt(1).assert(hasText("ha"))

        rule.onNodeWithText(TEXT_ALL_STATIONS).assertDoesNotExist()
        rule.onNodeWithText(TEXT_MATCHING_STATIONS).assertDoesNotExist()
        rule.onNodeWithText("No stations match the search.").assertIsDisplayed()
    }
}

private fun station(name: String, code: Int) = Station(
    passengerTraffic = true, type = Station.Type.Station, name = name, shortCode = "",
    code = code, countryCode = "FI", 10.0, 20.0
)
