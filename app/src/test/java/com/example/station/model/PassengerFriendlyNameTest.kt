package com.example.station.model

import com.google.common.truth.Truth.assertThat
import java.util.Locale
import org.junit.Test

class PassengerFriendlyNameTest {

    private val passengerFriendlyName = PassengerFriendlyName(
        fi = "Finnish name",
        sv = "Swedish name",
        en = "English name"
    )

    private val finnish = Locale("fi", "FI")
    private val swedish = Locale("sv", "FI")
    private val english = Locale("en")
    private val french = Locale("fr")

    @Test fun `get passenger friendly name in Finnish`() {
        val result = passengerFriendlyName.forLocale(finnish)
        assertThat(result).isEqualTo("Finnish name")
    }

    @Test fun `get passenger friendly name in Swedish`() {
        val result = passengerFriendlyName.forLocale(swedish)
        assertThat(result).isEqualTo("Swedish name")
    }

    @Test fun `get passenger friendly name in English`() {
        val result = passengerFriendlyName.forLocale(english)
        assertThat(result).isEqualTo("English name")
    }

    @Test fun `get passenger friendly name in French returns the English name`() {
        val result = passengerFriendlyName.forLocale(french)
        assertThat(result).isEqualTo("English name")
    }

}
