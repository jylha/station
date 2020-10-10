package com.example.station.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.ambientOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ConfigurationAmbient
import com.example.station.model.CauseCategories

@Composable fun DelayCauseProvider(
    causeCategories: CauseCategories?,
    content: @Composable () -> Unit
) {
    val categories = remember(causeCategories) {
        causeCategories ?: CauseCategories(emptyList(), emptyList(), emptyList())
    }
    Providers(DelayCauseAmbient provides categories) {
        content()
    }
}

@Composable fun delayCause(
    categoryId: Int,
    detailedCategoryId: Int? = null,
    thirdCategoryId: Int? = null
): String {
    val categories = DelayCauseAmbient.current
    val cause = when {
        thirdCategoryId != null ->
            categories.thirdLevelCategories.firstOrNull { it.id == thirdCategoryId }
        detailedCategoryId != null ->
            categories.detailedCategories.firstOrNull { it.id == detailedCategoryId }
        else ->
            categories.categories.firstOrNull { it.id == categoryId }
    }
    val localeList = ConfigurationAmbient.current.locales
    val locale = localeList.getFirstMatch(arrayOf("fi", "sv", "en"))
    return cause?.passengerFriendlyName?.run {
        when (locale?.language) {
            "fi" -> fi
            "en" -> en
            "sv" -> sv
            else -> en
        }
    } ?: "-"
}

private val DelayCauseAmbient = ambientOf<CauseCategories> {
    error("DelayCauseProvider is not set.")
}


