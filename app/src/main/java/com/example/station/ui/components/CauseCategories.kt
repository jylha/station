package com.example.station.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.ambientOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ConfigurationAmbient
import com.example.station.model.CauseCategories
import com.example.station.model.DelayCause

@Composable fun CauseCategoriesProvider(
    causeCategories: CauseCategories?,
    content: @Composable () -> Unit
) {
    val categories = remember(causeCategories) {
        causeCategories ?: CauseCategories(emptyList(), emptyList(), emptyList())
    }
    Providers(CauseCategoriesAmbient provides categories) {
        content()
    }
}

@Composable fun causeName(delayCause: DelayCause): String {
    val categories = CauseCategoriesAmbient.current
    val localeList = ConfigurationAmbient.current.locales
    return categories.passengerFriendlyNameFor(delayCause, localeList)
}

private val CauseCategoriesAmbient = ambientOf<CauseCategories> {
    error("CauseCategoriesProvider is not set.")
}


