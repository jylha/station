package dev.jylha.station.ui.home

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import dev.jylha.station.R
import dev.jylha.station.ui.common.Loading
import dev.jylha.station.ui.common.LocalLocationPermission
import dev.jylha.station.ui.common.landscapeOrientation
import dev.jylha.station.ui.common.portraitOrientation
import dev.jylha.station.ui.common.withPermission
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Home screen composable.
 * @param onNavigateToStations A callback function to navigate to the stations screen.
 * @param onNavigateToNearestStation A callback function to navigate to the timetable screen
 * of the nearest station.
 * @param onNavigateToTimetable A callback function to navigate to the timetable screen of
 * specified station.
 * @param onNavigateToAbout A callback function to navigate to the about screen.
 */
@OptIn(ExperimentalCoroutinesApi::class)
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
        onSelectStation = onNavigateToStations,
        onSelectNearestStation = onNavigateToNearestStation,
        onShowTimetable = onNavigateToTimetable,
        onAbout = onNavigateToAbout
    )
}

@Composable fun HomeScreen(
    viewState: HomeViewState,
    onSelectStation: () -> Unit = {},
    onSelectNearestStation: () -> Unit = {},
    onShowTimetable: (stationCode: Int) -> Unit = {},
    onAbout: () -> Unit = {},
) {
    Box(
        Modifier.background(
            if (MaterialTheme.colors.isLight) MaterialTheme.colors.primary
            else MaterialTheme.colors.background
        )
    ) {
        val locationPermission = LocalLocationPermission.current
        when {
            viewState.isLoadingSettings -> LoadingSettings()
            viewState.isLoadingStation -> LoadingStation()
            viewState.station != null -> onShowTimetable(viewState.station.code)
            else -> WelcomeCard(
                onSelectStation = onSelectStation,
                onShowNearestStation = {
                    withPermission(locationPermission) { granted ->
                        if (granted) onSelectNearestStation()
                        else onSelectStation()
                    }
                },
                onAbout = onAbout
            )
        }
    }
}

@Composable private fun LoadingSettings() {
    Loading(
        message = stringResource(R.string.message_loading_settings),
        textColor = MaterialTheme.colors.onPrimary.copy(alpha = 0.8f),
        indicatorColor = MaterialTheme.colors.onPrimary
    )
}

@Composable private fun LoadingStation() {
    Loading(
        message = stringResource(R.string.message_loading_timetable),
        textColor = MaterialTheme.colors.onPrimary.copy(alpha = 0.8f),
        indicatorColor = MaterialTheme.colors.onPrimary
    )
}

@Composable private fun WelcomeCard(
    onSelectStation: () -> Unit = {},
    onShowNearestStation: () -> Unit = {},
    onAbout: () -> Unit = {},
) {
    Card(
        Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .fillMaxSize()
    ) {
        Box {
            WelcomeAnimation(Modifier.width(400.dp).align(Alignment.Center))
            AboutButton(onClick = onAbout, Modifier.align(Alignment.TopEnd))
            Column(
                Modifier
                    .padding(
                        horizontal = 20.dp,
                        vertical = if (portraitOrientation()) 40.dp else 20.dp
                    )
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Greeting()
                    Spacer(Modifier.height(16.dp))
                    Introduction()
                }
                ButtonContainer(Modifier.padding(20.dp)) {
                    Button(
                        onSelectStation,
                        Modifier.width(180.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.label_select_station),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(Modifier.size(16.dp))
                    Button(onShowNearestStation, Modifier.width(180.dp)) {
                        Text(
                            text = stringResource(R.string.label_nearest_station),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable private fun AboutButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val buttonColor = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
    val label = stringResource(R.string.accessibility_label_show_application_info)
    IconButton(onClick, modifier) {
        Icon(Icons.Outlined.Info, contentDescription = label, tint = buttonColor)
    }
}

@Composable private fun Greeting(modifier: Modifier = Modifier) {
    val greetingText = stringResource(id = R.string.label_welcome)
    Text(greetingText, modifier, style = MaterialTheme.typography.h4)
}

@Composable private fun Introduction(modifier: Modifier = Modifier) {
    val introductionText = stringResource(id = R.string.text_introduction)
    val color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f)
    Text(
        introductionText, modifier, textAlign = TextAlign.Center, color = color,
        style = MaterialTheme.typography.body1.copy(
            lineHeight = MaterialTheme.typography.body1.fontSize * 1.5
        )
    )
}

@Composable private fun ButtonContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    if (landscapeOrientation()) {
        Row(modifier, verticalAlignment = Alignment.CenterVertically) {
            content()
        }
    } else {
        Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            content()
        }
    }
}

@Composable private fun WelcomeAnimation(modifier: Modifier = Modifier) {
    AndroidView(
        { context: Context ->
            val view = LottieAnimationView(context)
            view.setAnimation(R.raw.train_animation)
            view.repeatCount = 0
            view.repeatMode = LottieDrawable.RESTART
            view.playAnimation()
            view
        },
        modifier,
    )
}
