package dev.jylha.station.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ReportProblem
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import dev.jylha.station.ui.theme.StationTheme

/**
 * An error state composable for displaying error states in the application.
 * @param message The error message to be displayed.
 * @param modifier Modifier.
 * @param content Optional content to be displayed below error message.
 */
@Composable
fun ErrorState(
    message: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    Box(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        Column(
            Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                imageVector = Icons.Rounded.ReportProblem, contentDescription = null,
                Modifier.size(80.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.error)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(message, color = MaterialTheme.colorScheme.onBackground)
        }
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    horizontal = 20.dp,
                    vertical = if (portraitOrientation()) 60.dp else 20.dp
                )
        ) {
            content()
        }
    }
}

@PreviewLightDark
@Composable
private fun ErrorStatePreview() {
    StationTheme {
        ErrorState("Oops. Something went wrong.")
    }
}

@PreviewLightDark
@Composable
private fun ErrorStateWithContentPreview() {
    StationTheme {
        ErrorState("Oops. Something went wrong.") {
            Button(onClick = {}) {
                Text("Hello")
            }
        }
    }
}

