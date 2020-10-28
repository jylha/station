package com.example.station.ui.common

import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.example.station.R

/** An empty state composable for displaying empty states in the application. */
@Composable
fun EmptyState(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier.fillMaxSize().padding(20.dp),
        alignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier
                    .preferredSize(150.dp)
                    .background(
                        MaterialTheme.colors.primary.copy(alpha = 0.1f),
                        RoundedCornerShape(75.dp)
                    )
            ) {
                Icon(
                    asset = vectorResource(id = R.drawable.magnifying_class),
                    Modifier.preferredSize(100.dp).align(Alignment.Center),
                    tint = MaterialTheme.colors.primary.copy(alpha = 0.3f)
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text,
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
    EmptyState("(empty)")
}

@Preview(name = "Long text", showBackground = true)
@Composable
private fun PreviewEmptyWithLongText() {
    EmptyState(
        "We are terribly sorry, but none of those thing you were looking for, " +
                "could not be found. Maybe you should reconsider everything."
    )
}
