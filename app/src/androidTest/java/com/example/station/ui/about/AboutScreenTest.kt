package com.example.station.ui.about

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSubstring
import androidx.compose.ui.test.onNodeWithText
import androidx.ui.test.createComposeRule
import com.example.station.testutil.setThemedContent
import org.junit.Rule
import org.junit.Test

class AboutScreenTest {

    @get:Rule val rule = createComposeRule()

    @Test fun aboutScreen() {
        rule.setThemedContent { AboutScreen() }

        rule.onNodeWithText("About the application").assertIsDisplayed()
        rule.onNode(
            hasSubstring("Source of traffic information:") and
                    hasSubstring("Traffic Management Finland / digitraffic.fi, license CC 4.0 BY")
        ).assertIsDisplayed()
        rule.onNode(hasSubstring("Train animation by")).assertIsDisplayed()
    }
}
