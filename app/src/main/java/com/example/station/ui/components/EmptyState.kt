package com.example.station.ui.components

import androidx.compose.foundation.Box
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.example.station.R

/** An empty state composable for displaying empty states in the application. */
@Composable
fun EmptyState(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(modifier.fillMaxSize().padding(16.dp), gravity = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Stack {
                Box(
                    Modifier.preferredSize(150.dp).clip(RoundedCornerShape(75.dp)),
                    backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.1f)
                )
                Icon(
                    asset = vectorResource(id = R.drawable.magnifying_class),
                    Modifier.preferredSize(100.dp).align(Alignment.Center),
                    tint = MaterialTheme.colors.primary.copy(alpha = 0.3f)
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            Text(text, color = MaterialTheme.colors.onBackground.copy(alpha = 0.8f))
        }
    }
}

@Preview(name = "Empty", showBackground = true)
@Composable
private fun Empty() {
    EmptyState("(empty)")
}
