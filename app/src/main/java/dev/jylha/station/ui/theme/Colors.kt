package dev.jylha.station.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

private val red = Color(0xFFD32F2F)
private val blue = Color(0xFF005DB8)
private val lightBlue = Color(0xFF1976D2)
private val darkGreen = Color(0xFF48A868)
private val darkRed = Color(0xFFCF191F)
private val lightYellow = Color(0xFFF3EC2A)
private val darkYellow = Color(0xFFACA84A)
private val lightGray = Color(0xFFF2F2F2)
private val darkGray = Color(0xFF121212)

/** Custom color palette for the application. */
@Immutable
data class StationColorPalette(
    val trainIsNotReady: Color = Color.Blue,
    val trainOnStation: Color = Color.Green,
    val trainHasDepartedStation: Color = Color.Red,
    val trainOnRouteToStation: Color = Color.Yellow,
    val trainReachedDestination: Color = Color.DarkGray,
    val early: Color = darkGreen,
    val late: Color = darkRed,
    val delayed: Color = lightYellow,
    val isDark: Boolean
)

val LightStationColorPalette = StationColorPalette(
    delayed = darkYellow,
    isDark = false
)

val DarkStationColorPalette = StationColorPalette(
    isDark = true
)
