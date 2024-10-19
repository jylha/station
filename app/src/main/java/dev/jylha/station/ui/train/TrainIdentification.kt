package dev.jylha.station.ui.train

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Train
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import dev.jylha.station.R
import dev.jylha.station.model.Train
import dev.jylha.station.ui.theme.StationTheme

@Composable
fun TrainIdentification(train: Train, modifier: Modifier = Modifier) {
    train.run {
        if (isCommuterTrain()) {
            if (commuterLineId != null) {
                CommuterTrainIdentification(commuterLineId, modifier)
            } else {
                CommuterTrainIdentification(type, number, modifier)
            }
        } else {
            LongDistanceTrainIdentification(type, number, modifier)
        }
    }
}

@Composable
private fun LongDistanceTrainIdentification(type: String, number: Int, modifier: Modifier) {
    val label = when (type) {
        "IC" -> stringResource(R.string.accessibility_label_intercity_train, number)
        "S" -> stringResource(R.string.accessibility_label_pendolino_train, number)
        else -> stringResource(R.string.accessibility_label_long_distance_train, type, number)
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            Icons.Rounded.Train, contentDescription = null,
            Modifier
                .size(60.dp)
                .background(MaterialTheme.colorScheme.tertiary, CircleShape)
                .padding(4.dp),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onTertiary)
        )
        Spacer(Modifier.height(8.dp))
        Row {
            Text(
                "$type $number",
                modifier = Modifier.semantics { contentDescription = label },
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@Composable
private fun CommuterTrainIdentification(type: String, number: Int, modifier: Modifier) {
    val label = stringResource(R.string.accessibility_label_commuter_train, type, number)
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            Icons.Rounded.Train, contentDescription = null,
            Modifier
                .size(60.dp)
                .background(color = MaterialTheme.colorScheme.primary, CircleShape)
                .padding(4.dp),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
        )
        Spacer(Modifier.height(8.dp))
        Row {
            Text(
                "$type $number",
                modifier = Modifier.semantics { contentDescription = label },
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@Composable
private fun CommuterTrainIdentification(commuterLineId: String, modifier: Modifier) {
    val label = stringResource(R.string.accessibility_label_commuter_line, commuterLineId)
    Column(
        modifier = modifier
            .size(60.dp)
            .background(color = MaterialTheme.colorScheme.primary, CircleShape)
            .semantics { contentDescription = label },
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            commuterLineId, Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@PreviewLightDark
@Composable
private fun TrainIdentificationPreview(
    @PreviewParameter(TrainIdentificationPreviewParameterProvider::class) train: Train
) {
    StationTheme {
        Surface {
            TrainIdentification(train, Modifier.padding(16.dp))
        }
    }
}

internal class TrainIdentificationPreviewParameterProvider : PreviewParameterProvider<Train> {
    override val values: Sequence<Train>
        get() = sequenceOf(
            Train(1, "IC", Train.Category.LongDistance),
            Train(2, "S", Train.Category.LongDistance),
            Train(3, "P", Train.Category.LongDistance),
            Train(4, "R", Train.Category.Commuter),
            Train(5, "R", Train.Category.Commuter, commuterLineId = "Q"),
        )
}
