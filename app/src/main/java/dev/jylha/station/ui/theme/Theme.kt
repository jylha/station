package dev.jylha.station.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = blue,
    primaryVariant = lightBlue,
    secondary = red,
    onPrimary = Color.White,
    onSecondary = Color.White
)

private val LightColorPalette = lightColors(
    primary = blue,
    primaryVariant = lightBlue,
    secondary = red,
    onPrimary = Color.White,
    onSecondary = Color.White
)

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

private val LightStationColorPalette = StationColorPalette(
    delayed = darkYellow,
    isDark = false
)

private val DarkStationColorPalette = StationColorPalette(
    isDark = true
)

/** Composable that provides StationColorPalette and MaterialTheme elements. */
@Composable
fun StationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val stationColors = if (darkTheme) DarkStationColorPalette else LightStationColorPalette
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette

    ProvideStationColors(stationColors) {
        MaterialTheme(
            colors = colors,
            typography = typography,
            shapes = shapes
        ) {
            content()
        }
    }
}

/** An object for getting access to StationColorPalette. */
object StationTheme {
    val colors: StationColorPalette
        @Composable get() = LocalStationColorPalette.current
}

private val LocalStationColorPalette = staticCompositionLocalOf<StationColorPalette> {
    error("StationColorPalette is not set.")
}

@Composable
fun ProvideStationColors(
    colorPalette: StationColorPalette,
    content: @Composable () -> Unit
) {
    Providers(LocalStationColorPalette provides colorPalette) {
        content()
    }
}

