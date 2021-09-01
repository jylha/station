package dev.jylha.station.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
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
        BoxWithConstraints {
            val height = maxHeight
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (height >= 200.dp)
                    Icon(
                        painterResource(id = R.drawable.magnifying_class),
                        modifier = Modifier
                            .requiredSize(120.dp)
                            .background(
                                MaterialTheme.colors.primary.copy(alpha = 0.1f)
                                    .compositeOver(MaterialTheme.colors.surface),
                                RoundedCornerShape(percent = 50)
                            )
                            .padding(20.dp),
                        tint = MaterialTheme.colors.primary.copy(alpha = 0.3f)
                            .compositeOver(MaterialTheme.colors.surface),
                        contentDescription = null
                    )
                Text(
                    message,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                        .compositeOver(MaterialTheme.colors.surface),
                    lineHeight = MaterialTheme.typography.body1.fontSize * 1.5,
                    style = MaterialTheme.typography.body1,
                )
            }
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
