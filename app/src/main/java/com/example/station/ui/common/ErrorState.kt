package com.example.station.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ReportProblem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.emptyContent
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.station.ui.theme.StationTheme

/**
 * An error state composable for displaying error states in the application.
 * @param message The error message to be displayed.
 * @param modifier Modifier.
 * @param content Composable content to be displayed below error message.
 */
@Composable
fun ErrorState(
    message: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = emptyContent()
) {
    Box(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(20.dp)
    ) {
        Column(
            Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Rounded.ReportProblem.copy(defaultHeight = 80.dp, defaultWidth = 80.dp),
                tint = Color.Red.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(30.dp))
            Text(message, color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f))
        }
        Box(
            Modifier.align(Alignment.BottomCenter)
                .padding(
                    horizontal = 20.dp,
                    vertical = if (portraitOrientation()) 60.dp else 20.dp
                )
        ) {
            content()
        }
    }
}

@Preview
@Composable
private fun LightPreviewError() {
    StationTheme(darkTheme = false) {
        ErrorState("Oops. Something went wrong.")
    }
}

@Preview
@Composable
private fun DarkPreviewError() {
    StationTheme(darkTheme = true) {
        ErrorState("Oops. Something went wrong.")
    }
}
