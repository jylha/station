package com.example.station.model

import androidx.compose.runtime.Immutable

/**
 * Domain Model for train information.
 * @param number Train number.
 * @param type Train type: IC, P, S...
 */
@Immutable
data class Train(
    val number: Int,
    val type: String
)