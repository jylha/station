package dev.jylha.station.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

/** Composable that provides StationColorPalette and MaterialTheme elements. */
@Composable
fun StationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val stationColors = if (darkTheme) DarkStationColorPalette else LightStationColorPalette
    ProvideStationColors(stationColors) {
        MaterialTheme(
            colorScheme = if (darkTheme) StationDarkColorScheme else StationLightColorScheme,
            shapes = StationShapes,
            typography = StationTypography,
        ) {
            content()
        }
    }
}

/** An object for getting access to StationColorPalette. */
object StationTheme {
    val colors: StationColorPalette
        @ReadOnlyComposable
        @Composable
        get() = LocalStationColorPalette.current
}

private val LocalStationColorPalette =
    staticCompositionLocalOf { LightStationColorPalette }

@Composable
fun ProvideStationColors(
    colorPalette: StationColorPalette,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalStationColorPalette provides colorPalette) {
        content()
    }
}

