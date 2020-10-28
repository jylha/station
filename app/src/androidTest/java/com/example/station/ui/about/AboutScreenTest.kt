package com.example.station.ui.about

import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.createComposeRule
import androidx.ui.test.hasSubstring
import androidx.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class AboutScreenTest {

    @get:Rule val rule = createComposeRule()

    @Test fun aboutScreen() {
        rule.setContent { AboutScreen() }
        rule.onNodeWithText("About the application").assertIsDisplayed()
        rule.onNode(
            hasSubstring("Source of traffic information:") and
                    hasSubstring("Traffic Management Finland / digitraffic.fi, license CC 4.0 BY")
        ).assertIsDisplayed()
    }
}
