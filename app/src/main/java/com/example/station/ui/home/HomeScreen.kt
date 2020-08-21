package com.example.station.ui.home

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.example.station.Screen

@Composable
fun HomeScreen(
    navigateTo: (Screen) -> Unit
) {
    Column(horizontalGravity = Alignment.CenterHorizontally) {
        Text("Home screen")
        Button(onClick = { navigateTo(Screen.SelectStation) }) {
            Text("Select station")
        }
    }
}