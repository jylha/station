package dev.jylha.station.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val White = Color(0xFFFFFFFF)
private val LightGray1 = Color(0xFFf8f9fa)
private val LightGray2 = Color(0xFFe9ecef)
private val LightGray3 = Color(0xFFdee2e6)
private val LightGray4 = Color(0xFFced4da)
private val Gray = Color(0xFFadb5bd)
private val DarkGray4 = Color(0xFF6c757d)
private val DarkGray3 = Color(0xFF495057)
private val DarkGray2 = Color(0xFF343a40)
private val DarkGray1 = Color(0xFF212529)
private val Black = Color(0xFF000000)

private val Red = Color(0xFFD32F2F)
private val Blue = Color(0xFF005DB8)
private val LightBlue = Color(0xFF1976D2)
private val VLightBlue = Color(0xFFD6E3F0)


/** A dark color scheme for the application. */
val StationDarkColorScheme = darkColorScheme(
    primary = Blue,
    onPrimary = White,
    secondary = LightBlue,
    onSecondary = White,
    tertiary = Red,
    onTertiary = White,
    surface = DarkGray1,
    onSurface = White,
    surfaceVariant = DarkGray2,
    onSurfaceVariant = White,
    background = Black,
    onBackground = LightGray3,
)

/** A light color scheme for the application. */
val StationLightColorScheme = lightColorScheme(
    primary = Blue,
    onPrimary = White,
    secondary = LightBlue,
    onSecondary = White,
    tertiary = Red,
    onTertiary = White,
    surface = White,
    onSurface = Black,
    surfaceVariant = VLightBlue,
    onSurfaceVariant = DarkGray2,
    background = LightGray1,
    onBackground = Black,
)
