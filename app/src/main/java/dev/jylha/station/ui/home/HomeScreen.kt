package dev.jylha.station.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
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

/**
 * Home screen composable.
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
    val viewState by viewModel.state.collectAsState()

    HomeScreen(
        viewState,
        onShowStationSelection = onNavigateToStations,
        onShowNearestStation = onNavigateToNearestStation,
        onShowTimetable = onNavigateToTimetable,
        onShowInfo = onNavigateToAbout
    )
}

@Composable fun HomeScreen(
    state: HomeViewState,
    onShowStationSelection: () -> Unit = {},
    onShowNearestStation: () -> Unit = {},
    onShowTimetable: (stationCode: Int) -> Unit = {},
    onShowInfo: () -> Unit = {},
) {
    Box(
        Modifier.background(
            if (MaterialTheme.colors.isLight) MaterialTheme.colors.primary
            else MaterialTheme.colors.background
        )
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

@Composable private fun LoadingSettings() {
    Loading(
        message = stringResource(R.string.message_loading_settings),
        textColor = MaterialTheme.colors.onPrimary.copy(alpha = 0.8f)
            .compositeOver(MaterialTheme.colors.surface),
        indicatorColor = MaterialTheme.colors.onPrimary
    )
}

@Composable private fun LoadingStation() {
    Loading(
        message = stringResource(R.string.message_loading_timetable),
        textColor = MaterialTheme.colors.onPrimary.copy(alpha = 0.8f)
            .compositeOver(MaterialTheme.colors.surface),
        indicatorColor = MaterialTheme.colors.onPrimary
    )
}

@Composable private fun WelcomeCard(
    onShowStationSelection: () -> Unit,
    onShowNearestStation: () -> Unit,
    onShowInfo: () -> Unit,
    modifier: Modifier,
) {
    Card(
        modifier = modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp),
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
                    Button(onShowStationSelection, Modifier.width(180.dp)) {
                        Text(
                            text = stringResource(R.string.label_select_station),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.button
                        )
                    }
                    Button(onShowNearestStation, Modifier.width(180.dp)) {
                        Text(
                            text = stringResource(R.string.label_nearest_station),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.button
                        )
                    }
                }
            }
        }
    }
}

@Composable private fun AboutButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val buttonColor = with(MaterialTheme.colors) {
        (if (isLight) primary else onSurface).copy(alpha = 0.8f).compositeOver(surface)
    }
    val label = stringResource(R.string.accessibility_label_show_application_info)
    IconButton(onClick, modifier) {
        Icon(Icons.Outlined.Info, contentDescription = label, tint = buttonColor)
    }
}

@Composable private fun Greeting(modifier: Modifier = Modifier) {
    val color = MaterialTheme.colors.onSurface
    val text = stringResource(id = R.string.label_welcome)
    Text(text, modifier, color, style = MaterialTheme.typography.h4)
}

@Composable private fun Introduction(modifier: Modifier = Modifier) {
    val text = stringResource(id = R.string.text_introduction)
    val color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f)
        .compositeOver(MaterialTheme.colors.surface)
    Text(
        text, modifier, color, textAlign = TextAlign.Center,
        lineHeight = MaterialTheme.typography.body1.fontSize * 1.5,
        style = MaterialTheme.typography.body1
    )
}

@Composable private fun ButtonContainer(
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

@Composable private fun WelcomeAnimation(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.train_animation)
    )
    LottieAnimation(composition, modifier)
}
