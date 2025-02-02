package dev.jylha.station.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import dev.jylha.station.R
import dev.jylha.station.ui.common.Loading
import dev.jylha.station.ui.common.LocalLocationPermission
import dev.jylha.station.ui.common.landscapeOrientation
import dev.jylha.station.ui.common.portraitOrientation
import dev.jylha.station.ui.common.withPermission
import dev.jylha.station.ui.theme.StationTheme
import dev.jylha.station.ui.theme.backgroundColor
import dev.jylha.station.ui.theme.onBackgroundColor

/**
 * Home screen displays a welcome text and an animation, and contains buttons for navigating
 * to the stations screen, to the timetable screen of nearest station, and to the about screen.
 *
 * @param viewModel A view model for the home screen.
 * @param onNavigateToStations A callback function to navigate to the stations screen.
 * @param onNavigateToNearestStation A callback function to navigate to the timetable screen
 * of the nearest station.
 * @param onNavigateToTimetable A callback function to navigate to the timetable screen of
 * specified station.
 * @param onNavigateToAbout A callback function to navigate to the about screen.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToStations: () -> Unit,
    onNavigateToNearestStation: () -> Unit,
    onNavigateToTimetable: (stationCode: Int) -> Unit,
    onNavigateToAbout: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    HomeScreen(
        state = state,
        onShowStationSelection = onNavigateToStations,
        onShowNearestStation = onNavigateToNearestStation,
        onShowTimetable = onNavigateToTimetable,
        onShowInfo = onNavigateToAbout
    )
}

@Composable
fun HomeScreen(
    state: HomeViewState,
    onShowStationSelection: () -> Unit = {},
    onShowNearestStation: () -> Unit = {},
    onShowTimetable: (stationCode: Int) -> Unit = {},
    onShowInfo: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .background(backgroundColor())
            .safeDrawingPadding()
    ) {
        val locationPermission = LocalLocationPermission.current
        when {
            state.isLoadingSettings -> LoadingSettings()
            state.isLoadingStation -> LoadingStation()
            state.station != null -> LaunchedEffect(state.station.code) {
                onShowTimetable(state.station.code)
            }

            else -> WelcomeCard(
                onShowStationSelection = onShowStationSelection,
                onShowNearestStation = {
                    withPermission(locationPermission) { granted ->
                        if (granted) onShowNearestStation()
                        else onShowStationSelection()
                    }
                },
                onShowInfo = onShowInfo,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
private fun LoadingSettings() {
    Loading(
        message = stringResource(R.string.message_loading_settings),
        containerColor = backgroundColor(),
        textColor = onBackgroundColor(),
        indicatorColor = MaterialTheme.colorScheme.primary,
    )
}

@Composable
private fun LoadingStation() {
    Loading(
        message = stringResource(R.string.message_loading_timetable),
        containerColor = backgroundColor(),
        textColor = onBackgroundColor(),
        indicatorColor = MaterialTheme.colorScheme.primary,
    )
}

@Composable
private fun WelcomeCard(
    onShowStationSelection: () -> Unit,
    onShowNearestStation: () -> Unit,
    onShowInfo: () -> Unit,
    modifier: Modifier,
) {
    Card(
        modifier = modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        Box {
            WelcomeAnimation(Modifier.width(400.dp).align(Alignment.Center))
            AboutButton(onClick = onShowInfo, Modifier.align(Alignment.TopEnd))
            Column(
                Modifier
                    .padding(
                        horizontal = 20.dp,
                        vertical = if (portraitOrientation()) 60.dp else 20.dp
                    )
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Greeting()
                    Introduction()
                }
                ButtonContainer {
                    HomeScreenButton(onShowStationSelection) {
                        Icon(
                            Icons.Default.LocationCity,
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(R.string.label_select_station),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    HomeScreenButton(onShowNearestStation) {
                        Icon(
                            Icons.Default.MyLocation,
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(R.string.label_nearest_station),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeScreenButton(onClick: () -> Unit, content: @Composable RowScope.() -> Unit) {
    Button(
        onClick,
        modifier = Modifier.width(200.dp).heightIn(48.dp),
        shape = RoundedCornerShape(50),
        content = content,
    )
}

@Composable
private fun AboutButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val label = stringResource(R.string.accessibility_label_show_application_info)
    IconButton(onClick, modifier) {
        Icon(
            Icons.Outlined.Info,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun Greeting(modifier: Modifier = Modifier) {
    val color = MaterialTheme.colorScheme.onSurface
    val text = stringResource(id = R.string.label_welcome)
    Text(text, modifier, color, style = MaterialTheme.typography.headlineMedium)
}

@Composable
private fun Introduction(modifier: Modifier = Modifier) {
    val text = stringResource(id = R.string.text_introduction)
    Text(
        text, modifier,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        lineHeight = MaterialTheme.typography.bodyLarge.fontSize * 1.5,
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
private fun ButtonContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    if (landscapeOrientation()) {
        Row(modifier, Arrangement.spacedBy(16.dp), Alignment.CenterVertically) {
            content()
        }
    } else {
        Column(modifier, Arrangement.spacedBy(16.dp), Alignment.CenterHorizontally) {
            content()
        }
    }
}

@Composable
private fun WelcomeAnimation(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.train_animation)
    )
    LottieAnimation(composition, modifier)
}

@PreviewLightDark
@Composable
private fun HomeScreenPreview(
    @PreviewParameter(HomeScreenPreviewParameterProvider::class) state: HomeViewState
) {
    StationTheme {
        HomeScreen(state = state)
    }
}

internal class HomeScreenPreviewParameterProvider : PreviewParameterProvider<HomeViewState> {
    override val values: Sequence<HomeViewState>
        get() = sequenceOf(
            HomeViewState.Initial,
            HomeViewState(isLoadingSettings = true),
            HomeViewState(isLoadingStation = true),
        )
}
