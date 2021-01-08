package dev.jylha.station.ui.common


import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import dev.jylha.station.testutil.setThemedContent
import org.junit.Rule
import org.junit.Test

class EmptyStateTest {

    @get:Rule val rule = createComposeRule()

    @Test fun emptyStateDisplaysMessage() {
        rule.setThemedContent { EmptyState(text = "Nothing here!") }
        rule.onNodeWithText("Nothing here!").assertIsDisplayed()
    }
}
