package dev.jylha.station.ui.common

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun RefreshIndicator() {
    Surface(elevation = 10.dp, shape = CircleShape) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(36.dp)
                .padding(4.dp)
        )
    }
}
