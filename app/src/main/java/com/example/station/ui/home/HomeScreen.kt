package com.example.station.ui.home

import androidx.compose.foundation.Box
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import com.example.station.ui.Screen
import com.example.station.ui.components.LoadingMessage
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
            LoadingMessage(message = "Loading application settings...")
        } else if (false && viewState.station != null) {
            navigateTo(Screen.Timetable(viewState.station!!))
        } else {
            WelcomeCard(onSelect = { navigateTo(Screen.SelectStation) })
        }
    }
}

@Composable
private fun WelcomeCard(onSelect: () -> Unit) {
    Card(

        Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .fillMaxSize()
    ) {
        Column(
            horizontalGravity = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Welcome!", style = MaterialTheme.typography.h4)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onSelect) {
                Text("Select station")
            }
        }
    }
}
