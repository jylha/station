package com.example.station.ui.components

import androidx.ui.test.android.createAndroidComposeRule
import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.onNodeWithText
import com.example.station.ui.MainActivity
import com.example.station.ui.theme.StationTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class EmptyStateTest {

    @get:Rule
    val composeTestRule =
        createAndroidComposeRule<MainActivity>(disableTransitions = true)

    @Before fun setup() {
        composeTestRule.setContent {
            StationTheme {
                EmptyState(text = "Hello!")
            }
        }
    }

    @Test fun emptyStateDisplaysMessage() {
        onNodeWithText("Hello!").assertIsDisplayed()
    }
}
