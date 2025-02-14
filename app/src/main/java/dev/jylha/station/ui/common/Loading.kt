package dev.jylha.station.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import dev.jylha.station.ui.theme.StationTheme

/**
 * A composable that displays a progress indicator along with the given [message].
 */
@Composable
fun Loading(
    message: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.background,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    indicatorColor: Color = MaterialTheme.colorScheme.primary
) {
    Surface(
        color = containerColor,
    ) {
        Box(
            modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = indicatorColor)
                Spacer(modifier = Modifier.height(16.dp))
                Text(message, textAlign = TextAlign.Center, color = textColor)
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun LoadingPreview() {
    StationTheme {
        Loading("Loading something...")
    }
}
