package com.example.station.ui.about

import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.example.station.R
import com.example.station.ui.components.portraitOrientation

@Preview
@Composable
fun AboutScreen() {
    AboutCard()
}

@Composable private fun AboutCard() {
    val aboutLabel = stringResource(R.string.label_about)
    val sourceLabel = stringResource(R.string.label_source)
    val sourceText = stringResource(R.string.text_source)

    val contentColor = MaterialTheme.colors.onSurface.copy(alpha = 0.8f)
    Card(
        Modifier
            .background(
                if (MaterialTheme.colors.isLight) MaterialTheme.colors.primary
                else MaterialTheme.colors.background
            )
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .fillMaxSize()
    ) {
        Column(
            Modifier.padding(
                horizontal = 20.dp,
                vertical = if (portraitOrientation()) 40.dp else 20.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(aboutLabel, style = MaterialTheme.typography.h5)
            Spacer(Modifier.height(16.dp))
            Text(
                "$sourceLabel $sourceText",
                color = contentColor,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body1.copy(
                    lineHeight = MaterialTheme.typography.body1.fontSize * 1.5
                )
            )
        }
    }
}
