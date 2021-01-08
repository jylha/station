package dev.jylha.station.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.ambientOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.AmbientConfiguration
import dev.jylha.station.model.CauseCategories
import dev.jylha.station.model.DelayCause

/**
 * Composable function that provides passenger friendly descriptions for delay causes. The content
 * of this composable can use [causeName] function to query descriptions for delay causes.
 * @param causeCategories Cause categories that contain descriptions of delay causes.
 * @param content Composable content.
 */
@Composable fun CauseCategoriesProvider(
    causeCategories: CauseCategories?,
    content: @Composable () -> Unit
) {
    val categories = remember(causeCategories) {
        causeCategories ?: CauseCategories(emptyList(), emptyList(), emptyList())
    }
    Providers(AmbientCauseCategories provides categories) {
        content()
    }
}

/** Returns a passenger friendly name for given [delayCause] in preferred language. */
@Composable fun causeName(delayCause: DelayCause): String {
    val categories = AmbientCauseCategories.current
    val localeList = AmbientConfiguration.current.locales
    return categories.passengerFriendlyNameFor(delayCause, localeList)
}

private val AmbientCauseCategories = ambientOf<CauseCategories> {
    error("CauseCategories is not set.")
}


