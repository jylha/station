package dev.jylha.station.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowRightAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import dev.jylha.station.R
import dev.jylha.station.ui.theme.StationTheme

/**
 *  A composable that displays given origin and destination station for a train route.
 *  @param origin Name of train's origin station.
 *  @param destination Name of train's destination station.
 *  @param modifier Modifier.
 *  @param textStyle Text style used for station name texts.
 *  @param fontWeight Font weight used for station names texts.
 */
@Composable
fun TrainRoute(
    origin: String,
    destination: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
    fontWeight: FontWeight = FontWeight.SemiBold,
) {
    val trainOriginDescription =
        stringResource(R.string.accessibility_label_from_station, origin)
    val trainDestinationDescription =
        stringResource(R.string.accessibility_label_to_station, destination)

    ConstraintLayout(modifier) {
        val originRef = createRef()
        val destinationRef = createRef()
        val iconRef = createRef()

        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowRightAlt, contentDescription = null,
            modifier = Modifier.padding(horizontal = 4.dp).constrainAs(iconRef) {
                centerTo(parent)
            }
        )
        Text(
            text = origin,
            style = textStyle,
            fontWeight = fontWeight,
            textAlign = TextAlign.End,
            modifier = Modifier
                .semantics { contentDescription = trainOriginDescription }
                .constrainAs(originRef) {
                    linkTo(top = parent.top, bottom = parent.bottom)
                    linkTo(start = parent.start, end = iconRef.start)
                    width = Dimension.fillToConstraints
                    height = Dimension.preferredWrapContent
                }
        )
        Text(
            text = destination,
            style = textStyle,
            fontWeight = fontWeight,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .semantics { contentDescription = trainDestinationDescription }
                .constrainAs(destinationRef) {
                    linkTo(top = parent.top, bottom = parent.bottom)
                    linkTo(start = iconRef.end, end = parent.end)
                    width = Dimension.fillToConstraints
                    height = Dimension.preferredWrapContent
                }
        )
    }
}

@PreviewLightDark
@Composable
private fun TrainRoutePreview() {
    StationTheme {
        Surface(Modifier.fillMaxWidth()) {
            TrainRoute(origin = "Origin", destination = "Destination")
        }
    }
}
