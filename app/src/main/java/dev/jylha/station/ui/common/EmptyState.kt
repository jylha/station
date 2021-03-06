package dev.jylha.station.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.jylha.station.R
import dev.jylha.station.ui.theme.StationTheme

/**
 * A composable for displaying empty states in the application.
 * @param message A description for the empty state.
 * @param modifier Optional modifier for the composable.
 */
@Composable
fun EmptyState(
    message: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier
                    .size(140.dp)
                    .background(
                        MaterialTheme.colors.primary.copy(alpha = 0.1f),
                        RoundedCornerShape(70.dp)
                    )
            ) {
                Icon(
                    painterResource(id = R.drawable.magnifying_class),
                    modifier = Modifier.size(100.dp).align(Alignment.Center),
                    tint = MaterialTheme.colors.primary.copy(alpha = 0.3f),
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                message,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f),
                style = MaterialTheme.typography.body1.copy(
                    lineHeight = MaterialTheme.typography.body1.fontSize * 1.5
                )
            )
        }
    }
}

@Preview(name = "Empty", showBackground = true)
@Composable
private fun Empty() {
    StationTheme(darkTheme = true) {
        EmptyState("(empty)")
    }
}

@Preview(name = "Long text", showBackground = true)
@Composable
private fun PreviewEmptyWithLongText() {
    EmptyState(
        "We are terribly sorry, but none of those thing you were looking for, " +
                "could not be found. Maybe you should reconsider everything."
    )
}
