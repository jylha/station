package com.example.station.ui.common

import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.createComposeRule
import androidx.ui.test.onNodeWithText
import com.example.station.testutil.setThemedContent
import org.junit.Rule
import org.junit.Test

class EmptyStateTest {

    @get:Rule val rule = createComposeRule()

    @Test fun emptyStateDisplaysMessage() {
        rule.setThemedContent { EmptyState(text = "Nothing here!") }
        rule.onNodeWithText("Nothing here!").assertIsDisplayed()
    }
}
