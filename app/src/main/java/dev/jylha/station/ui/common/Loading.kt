package dev.jylha.station.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * A composable that displays a progress indicator along with the given [message].
 */
@Composable
fun Loading(
    message: String,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colors.onBackground,
    indicatorColor: Color = MaterialTheme.colors.primary
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

@Preview(name = "Loading message.", showBackground = true)
@Composable
private fun PreviewLoading() {
    Loading("Loading something...")
}
