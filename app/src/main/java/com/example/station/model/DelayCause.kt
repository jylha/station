package com.example.station.model

import android.os.LocaleList
import androidx.compose.runtime.Immutable
import java.util.Locale

/** Cause of delay. */
@Immutable
data class DelayCause(
    val categoryId: Int,
    val detailedCategoryId: Int? = null,
    val thirdLevelCategoryId: Int? = null,
)

/** Single delay cause category. */
@Immutable
data class CauseCategory(
    val id: Int,
    val name: String,
    val passengerFriendlyName: PassengerFriendlyName? = null
)

/** Passenger friendly names for a delay cause category in each supported language. */
@Immutable
data class PassengerFriendlyName(
    val fi: String,
    val en: String,
    val sv: String
) {
    /** Returns the name for the specified locale. Defaults to English. */
    fun forLocale(locale: Locale?): String {
        return when (locale?.language) {
            "fi" -> fi
            "sv" -> sv
            else -> en
        }
    }
}

/** All delay cause categories divided in three different levels. */
@Immutable
data class CauseCategories(
    val categories: List<CauseCategory>,
    val detailedCategories: List<CauseCategory>,
    val thirdLevelCategories: List<CauseCategory> = emptyList()
) {
    /** Returns the passenger friendly name for given delay cause in preferred language. */
    fun passengerFriendlyNameFor(delayCause: DelayCause, localeList: LocaleList): String {
        val locale = localeList.getFirstMatch(arrayOf("fi", "sv", "en"))
        return passengerFriendlyNameFor(delayCause, locale)
    }

    /** Returns the passenger friendly name for given delay cause in specified language. */
    fun passengerFriendlyNameFor(delayCause: DelayCause, locale: Locale?): String {
        val categoryName = with(delayCause) {
            passengerFriendlyName(thirdLevelCategoryId, thirdLevelCategories)
                ?.run { return@with this }
            passengerFriendlyName(detailedCategoryId, detailedCategories)
                ?.run { return@with this }
            passengerFriendlyName(categoryId, categories)
                ?.run { return@with this }
            return@with null
        }
        // TODO: 13.10.2020 Add localized message when no passenger friendly name is available?
        return categoryName?.forLocale(locale) ?: "-"
    }
}

private fun passengerFriendlyName(
    categoryId: Int?, categories: List<CauseCategory>
): PassengerFriendlyName? {
    return if (categoryId == null) null
    else categories.firstOrNull { category -> category.id == categoryId }
        ?.passengerFriendlyName
}


