package dev.jylha.station.ui.common

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.jylha.station.ui.theme.StationTheme

/**
 * A composable that displays a progress indicator along with the given [message].
 */
@Composable
fun Loading(
    message: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colors.background,
    textColor: Color = MaterialTheme.colors.onBackground,
    indicatorColor: Color = MaterialTheme.colors.primary
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

@Preview(name = "Loading - light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Loading - dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewLoading() {
    StationTheme {
        Loading("Loading something...")
    }
}
