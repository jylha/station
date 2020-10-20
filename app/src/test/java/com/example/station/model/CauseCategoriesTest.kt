package com.example.station.model

import com.google.common.truth.Truth.assertThat
import java.util.Locale
import org.junit.Test

class CauseCategoriesTest {

    private val causeCategories = CauseCategories(
        categories = listOf(
            CauseCategory(1, "1", PassengerFriendlyName("1 - fi", "1 - en", "1 - sv")),
            CauseCategory(2, "2", PassengerFriendlyName("2 - fi", "2 - en", "2 - sv")),
            CauseCategory(3, "3", passengerFriendlyName = null)
        ),
        detailedCategories = listOf(
            CauseCategory(21, "21", PassengerFriendlyName("21 - fi", "21 - en", "21 - sv")),
            CauseCategory(22, "22", PassengerFriendlyName("22 - fi", "22 - en", "22 - sv")),
            CauseCategory(23, "23", passengerFriendlyName = null)
        ),
        thirdLevelCategories = listOf(
            CauseCategory(301, "301", PassengerFriendlyName("301 - fi", "301 - en", "301 - sv")),
            CauseCategory(302, "302", PassengerFriendlyName("302 - fi", "302 - en", "302 - sv")),
            CauseCategory(303, "303", passengerFriendlyName = null)
        )
    )

    private val finnish = Locale("fi", "FI")
    private val swedish = Locale("sv", "FI")
    private val english = Locale("en")

    @Test fun `top level category name in Finnish`() {
        val cause = DelayCause(1)
        val result = causeCategories.passengerFriendlyNameFor(cause, finnish)
        assertThat(result).isEqualTo("1 - fi")
    }

    @Test fun `top level cause in Swedish`() {
        val cause = DelayCause(1)
        val result = causeCategories.passengerFriendlyNameFor(cause, swedish)
        assertThat(result).isEqualTo("1 - sv")
    }

    @Test fun `top level cause English`() {
        val cause = DelayCause(1)
        val result = causeCategories.passengerFriendlyNameFor(cause, english)
        assertThat(result).isEqualTo("1 - en")
    }

    @Test fun `detailed cause in Finnish`() {
        val cause = DelayCause(2, 21)
        val result = causeCategories.passengerFriendlyNameFor(cause, finnish)
        assertThat(result).isEqualTo("21 - fi")
    }

    @Test fun `third level cause in Swedish`() {
        val cause = DelayCause(1, 22, 301)
        val result = causeCategories.passengerFriendlyNameFor(cause, swedish)
        assertThat(result).isEqualTo("301 - sv")
    }

    @Test fun `third level cause without locale defaults to english`() {
        val cause = DelayCause(1, 21, 302)
        val result = causeCategories.passengerFriendlyNameFor(cause, null)
        assertThat(result).isEqualTo("302 - en")
    }

    @Test fun `third level cause without passenger friendly name returns detailed category name`() {
        val cause = DelayCause(1, 21, 303)
        val result = causeCategories.passengerFriendlyNameFor(cause, swedish)
        assertThat(result).isEqualTo("21 - sv")
    }

    @Test fun `detailed cause without passenger friendly name returns category name`() {
        val cause = DelayCause(1, 23)
        val result = causeCategories.passengerFriendlyNameFor(cause, finnish)
        assertThat(result).isEqualTo("1 - fi")
    }

    @Test fun `top level cause without passenger friendly name`() {
        val cause = DelayCause(3)
        val result = causeCategories.passengerFriendlyNameFor(cause, english)
        assertThat(result).isEqualTo("-")
    }
}