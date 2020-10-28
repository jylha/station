package com.example.station.ui.common

import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.createComposeRule
import androidx.ui.test.onNodeWithText
import com.example.station.ui.theme.StationTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class EmptyStateTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Before fun setup() {
        composeTestRule.setContent {
            StationTheme {
                EmptyState(text = "Hello!")
            }
        }
    }

    @Test fun emptyStateDisplaysMessage() {
        composeTestRule.onNodeWithText("Hello!").assertIsDisplayed()
    }
}
