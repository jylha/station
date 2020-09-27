package com.example.station.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticAmbientOf
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = blue,
    primaryVariant = blue_light,
    secondary = red,
    onPrimary = Color.White,
    onSecondary = Color.White
)

private val LightColorPalette = lightColors(
    primary = blue,
    primaryVariant = blue_light,
    secondary = red,
    onPrimary = Color.White,
    onSecondary = Color.White
)

/** Custom color palette for the application. */
class StationColorPalette(
    val trainIsNotReady: Color = Color.Blue,
    val trainOnStation: Color = Color.Green,
    val trainHasDepartedStation: Color = Color.Red,
    val trainOnRouteToStation: Color = Color.Yellow,
    val trainReachedDestination: Color = Color.DarkGray,
    val isDark: Boolean
)

private val LightStationColorPalette = StationColorPalette(
    isDark = false
)

private val DarkStationColorPalette = StationColorPalette(
    isDark = true
)


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
            shapes = shapes,
            content = content
        )
    }
}

object StationTheme {
    @Composable
    val colors: StationColorPalette
        get() = StationColorAmbient.current
}

private val StationColorAmbient = staticAmbientOf<StationColorPalette> {
    error("StationColorPalette is not set.")
}

@Composable
fun ProvideStationColors(
    colors: StationColorPalette,
    content: @Composable () -> Unit
) {
    val colorPalette = remember { colors }
    Providers(StationColorAmbient provides colorPalette, children = content)
}

