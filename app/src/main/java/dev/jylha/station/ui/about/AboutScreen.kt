package dev.jylha.station.ui.about

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.jylha.station.R
import dev.jylha.station.ui.common.portraitOrientation
import dev.jylha.station.ui.theme.StationTheme

/**
 * About screen.
 *
 * @param onNavigateToOssLicenses Called to navigate to open source licenses.
 */
@Composable
fun AboutScreen(
    onNavigateToOssLicenses: () -> Unit
) {
    val aboutLabel = stringResource(R.string.label_about_application)
    val informationSourceLabel = stringResource(R.string.label_information_source)
    val informationSourceText = stringResource(R.string.text_information_source)
    val trainAnimationCreditText = stringResource(R.string.text_train_animation_credit)

    val backgroundColor = with(MaterialTheme.colors) {
        if (isLight) primary else background
    }
    val surfaceColor = MaterialTheme.colors.surface
    val contentColor = MaterialTheme.colors.onSurface.copy(alpha = 0.8f)
        .compositeOver(surfaceColor)
    val textStyle = MaterialTheme.typography.body1.copy(
        lineHeight = MaterialTheme.typography.body1.fontSize * 1.5
    )

    Card(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .fillMaxSize(),
        backgroundColor = surfaceColor,
        contentColor = contentColor,
    ) {
        Box(
            Modifier.padding(
                horizontal = 20.dp,
                vertical = if (portraitOrientation()) 40.dp else 20.dp
            ),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(aboutLabel, style = MaterialTheme.typography.h5)
                Text(
                    "$informationSourceLabel $informationSourceText",
                    color = contentColor,
                    textAlign = TextAlign.Center,
                    style = textStyle
                )
                Text(
                    trainAnimationCreditText,
                    color = contentColor,
                    textAlign = TextAlign.Center,
                    style = textStyle
                )
            }
            TextButton(
                onClick = onNavigateToOssLicenses,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Text(text = stringResource(R.string.label_oss_licenses))
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewAboutScreen() {
    StationTheme {
        AboutScreen(onNavigateToOssLicenses = {})
    }
}
