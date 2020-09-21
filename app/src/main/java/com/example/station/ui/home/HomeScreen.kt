package com.example.station.ui.home

import android.content.Context
import androidx.compose.foundation.Box
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.viewModel
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.station.R
import com.example.station.ui.Screen
import com.example.station.ui.components.Loading
import com.example.station.ui.components.landscapeOrientation
import com.example.station.ui.components.portraitOrientation
import com.example.station.ui.theme.blue
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun HomeScreen(
    navigateTo: (Screen) -> Unit
) {
    val viewModel = viewModel<HomeViewModel>()
    val viewState by viewModel.state.collectAsState()

    Box(
        backgroundColor = if (MaterialTheme.colors.isLight) blue else Color.Black
    ) {
        if (viewState.loading) {
            Loading(stringResource(R.string.message_loading_settings))
        } else if (false && viewState.station != null) {
            navigateTo(Screen.Timetable(viewState.station!!))
        } else {
            WelcomeCard(
                onSelectStation = { navigateTo(Screen.SelectStation) },
                onShowNearestStation = {}
            )
        }
    }
}

@Composable private fun WelcomeCard(
    onSelectStation: () -> Unit = {},
    onShowNearestStation: () -> Unit = {}
) {
    Card(
        Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .fillMaxSize()
    ) {
        Stack {
            WelcomeAnimation(Modifier.width(400.dp).align(Alignment.Center))
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
                    Button(onSelectStation, Modifier.width(180.dp)) {
                        Text(stringResource(R.string.label_select_station))
                    }
                    Spacer(Modifier.size(16.dp))
                    Button(onShowNearestStation, Modifier.width(180.dp), enabled = false) {
                        Text(stringResource(R.string.label_nearest_station))
                    }
                }
            }
        }
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
        style = MaterialTheme.typography.body1,
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
    var animationView by remember { mutableStateOf<LottieAnimationView?>(null) }

    AndroidView({ context: Context ->
        val view = LottieAnimationView(context)
        view.setAnimation("train-animation.json")
        view.repeatCount = 0
        view.repeatMode = LottieDrawable.RESTART
        view.playAnimation()
        animationView = view
        view
    },
        modifier,
        {})
}
