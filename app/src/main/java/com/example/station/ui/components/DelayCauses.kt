package com.example.station.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.ambientOf
import androidx.compose.runtime.remember
import com.example.station.model.CauseCategories
import timber.log.Timber

@Composable fun DelayCauseProvider(
    causeCategories: CauseCategories?,
    content: @Composable () -> Unit
) {
    Timber.d("Update delay causes")
    val categories = remember(causeCategories) {
        causeCategories ?: CauseCategories(emptyList(), emptyList(), emptyList())}
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
        detailedCategoryId != null ->
            categories.detailedCategories.firstOrNull { it.id == detailedCategoryId }
        else ->
            categories.categories.firstOrNull { it.id == categoryId }
    }
    return cause?.name ?: "<unknown>"
}

private val DelayCauseAmbient = ambientOf<CauseCategories> {
    error("DelayCauseProvider is not set.")
}


