package com.example.station.ui.components

import androidx.compose.foundation.Box
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview


/**
 * A loading message composable that displays a progress indicator along with the given [message].
 */
@Composable
fun LoadingMessage(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier.fillMaxSize(),
        gravity = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(message)
        }
    }
}


@Preview(name = "Loading message.", showBackground = true)
@Composable
private fun LoadingMessage() {
    LoadingMessage("Loading something...")
}
